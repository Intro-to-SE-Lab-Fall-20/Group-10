import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ReplyController {

    //gui elements
    @FXML public static AnchorPane parent;
    @FXML private TextField replyTo;
    @FXML private ChoiceBox<String> attachmentsChoice;
    @FXML private TextField replySubject;
    @FXML private TextArea emailContent;
    @FXML private Button attachButton;
    @FXML private Button removeAttachments;
    @FXML private Button discardButton;
    @FXML private Button replyButton;

    private LinkedList<File> additionalAttachments = EmailController.currentMessageAttachments;
    public ObservableList attachmentsDisplay = FXCollections.observableArrayList();

    private Thread loadingAttachThread;

    //prepare the reply
    @FXML
    public void initialize() {
        try {
            removeAttachments.setDisable(true);
            replyButton.setDisable(true);
            discardButton.setDisable(true);
            attachButton.setDisable(true);

            new Thread(() -> {
                if (EmailController.currentMessageAttachments == null) {
                    Main.startWorking("Preparing reply...",0);
                    EmailController.initAttachments();
                    Main.startWorking("Prepared!",1000);
                }

                Platform.runLater(() -> {
                    replySubject.setText("RE: " + EmailController.currentMessageSubject);
                    emailContent.setText("\n\n-----------------------------------------------------------------\n" + EmailController.currentMessageBody);

                    additionalAttachments = new LinkedList<>(EmailController.currentMessageAttachments);

                    attachmentsChoice.getItems().clear();
                    attachmentsDisplay.clear();

                    for (File attachment : additionalAttachments) {
                        String currentAttachDisp = attachment.getName() + " - " + getDisplayFileSize(attachment);
                        attachmentsDisplay.add(currentAttachDisp);
                    }

                    attachmentsChoice.setItems(attachmentsDisplay);

                    removeAttachments.setDisable(false);
                    replyButton.setDisable(false);
                    discardButton.setDisable(false);
                    attachButton.setDisable(false);

                    if (EmailController.currentMessageAttachments.size() > 0)
                        Platform.runLater(() -> attachmentsChoice.getSelectionModel().select(0));
                });
            }).start();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send our reply
    @FXML
    private void sendReply(ActionEvent event) {
        String replyingSubject = replySubject.getText();
        String additionalRecipients = replyTo.getText();
        String emailCont = emailContent.getText();

        if (!validEmailAddr(additionalRecipients) && additionalRecipients.trim().length() > 0) {
            showPopupMessage("Please check your additional recipients email addresses", Main.primaryStage);
        }

        else {
            try {
                if (replyingSubject.length() == 0) {
                    showPopupMessage("Please note there is no subject", Main.primaryStage);
                }

                if (emailCont.length() == 0) {
                    showPopupMessage("Please note there is no message body", Main.primaryStage);
                }

                //init email and password
                String ourEmail = MainController.emailAddress;
                StringBuilder passwordBuilder = new StringBuilder();
                for (int i = 0; i < MainController.password.length ; i++)
                    passwordBuilder.append(MainController.password[i]);

                Properties props = new Properties();
                props.put("mail.smtp.auth", true);
                props.put("mail.smtp.starttls.enable", true);
                props.put("mail.smtp.host", getEmailHost(ourEmail));
                props.put("mail.smtp.port", 587);

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(ourEmail, passwordBuilder.toString()); }
                        });

                Message message = EmailController.currentMessage;
                Message replyingMessage = message.reply(false);

                replyingMessage.setSubject(replyingSubject);

                InternetAddress[] addresses = InternetAddress.parse(additionalRecipients);
                replyingMessage.addRecipients(Message.RecipientType.TO, addresses);

                Multipart emailContent = new MimeMultipart();

                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setContent(emailCont,"text/html");

                emailContent.addBodyPart(textBodyPart);

                if (attachmentsSize() >= 25) {
                    showPopupMessage("Sorry, but your attachments exceed the limit of 25MB. " +
                            "Please remove some files. Currently at " + String.format("%.2f", attachmentsSize()) + "MB", Main.primaryStage);
                }

                else {
                    addAttachments(emailContent);
                    replyingMessage.setContent(emailContent);
                    Transport.send(replyingMessage);
                    showPopupMessage("Email reply sent successfully", Main.primaryStage);
                    goBack(null);
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //attachment size in MB
    private double attachmentsSize() {
        double megaBytes = 0;

        for (File attachment : additionalAttachments)
            megaBytes += attachment.length() / 1024.0 / 1024.0;

        return megaBytes;
    }

    //this method adds attachments to the actual email that we are going to send
    private void addAttachments(Multipart multipart) {
        try {
            if (additionalAttachments != null && !additionalAttachments.isEmpty()) {
                for (File file : additionalAttachments) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    attachment.attachFile(file);
                    multipart.addBodyPart(attachment);
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //validate a email address
    private boolean validEmailAddr(String addr) {
        String[] ccs = addr.split(",");

        for (String email: ccs) {
            if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
                    "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])" +
                    "|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-" +
                    "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
                return false;
        }

        return true;
    }

    @FXML
    private void removeFile(ActionEvent e) {
        int index = attachmentsChoice.getSelectionModel().getSelectedIndex();

        if (index >= 0 && index < attachmentsDisplay.size()) {
            attachmentsDisplay.remove(index);
            additionalAttachments.remove(index);

            attachmentsChoice.setItems(attachmentsDisplay);

            if (attachmentsDisplay.size() > 0)
                attachmentsChoice.getSelectionModel().select(0);
        }
    }

    //add attachments to tableview and attachment list but not the reply multipart yet
    @FXML
    private void addFiles(ActionEvent e) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Add attachments");

            List<File> attachmentList = fc.showOpenMultipleDialog(null);

            if (attachmentList == null)
                return;

            LinkedList<File> listAttachments = new LinkedList<>(attachmentList);

            if (listAttachments != null) {
                for (File f : listAttachments) {
                    boolean skip = false;

                    for (File compFile : additionalAttachments)
                        if (compFile.getName().equalsIgnoreCase(f.getName()))
                            skip = true;

                    if (!skip)
                        additionalAttachments.add(f);
                }

                attachmentsChoice.getItems().clear();
                attachmentsDisplay.clear();

                for (File attachment : additionalAttachments) {
                    String currentAttachDisp = attachment.getName() + " - " + getDisplayFileSize(attachment);
                    attachmentsDisplay.add(currentAttachDisp);
                }

                attachmentsChoice.setItems(attachmentsDisplay);
                attachmentsChoice.getSelectionModel().select(0);
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //step back to previous screen
    @FXML
    private void goBack(ActionEvent event) {
        try {
            Scene currentScene = attachButton.getScene();
            StackPane pc = (StackPane) currentScene.getRoot();
            EmailController.root.translateXProperty().set(0);
            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(EmailController.root.translateXProperty(), currentScene.getWidth(), Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(e -> pc.getChildren().remove(EmailController.root));
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        ViewController.clearLocalAttachments();

        System.exit(0);
    }

    //returns a representation if a file in MB or KB with 2 decimal places
    private String getDisplayFileSize(File f) {
        DecimalFormat threeDecimal = new DecimalFormat("#.##");
        double mbSize = f.length() / 1024.0 / 1024.0;

        if (mbSize < 1)
            return threeDecimal.format((mbSize * 1024.0)) + " KB";
        else
            return threeDecimal.format(mbSize) + " MB";
    }

    private String getEmailAddress() {
        return MainController.emailAddress;
    }

    //make sure we support the email server
    private String getEmailHost(String email) throws Exception {
        if (email.endsWith("gmail.com"))
            return "smtp.gmail.com";
        else if (email.endsWith("yahoo.com"))
            return "smtp.mail.yahoo.com";
        else if (email.endsWith("outlook.com"))
            return "smtp.office365.com";
        else
            throw new IllegalAccessException("Unsupported email host");
    }

    //fx popup
    private Popup createPopup(final String message) {
        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        Label label = new Label(message);
        label.setOnMouseReleased(e -> popup.hide());
        label.getStylesheets().add("DefaultStyle.css");
        label.getStyleClass().add("popup");
        popup.getContent().add(label);
        return popup;
    }

    private void showPopupMessage(final String message, final Stage stage) {
        final Popup popup = createPopup(message);
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + stage.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(stage.getY() + 20);
        });
        popup.show(stage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }
}
