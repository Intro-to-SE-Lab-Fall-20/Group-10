import javafx.animation.*;
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
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ComposeController {

    public Button discardButton;
    public Button sendButton;

    //attachment list
    private LinkedList<File> attachments = new LinkedList<>();
    public ObservableList attachmentsDisplay = FXCollections.observableArrayList();

    //ui elements
    @FXML
    public Button attachButton;
    public Button removeAttachment;
    public ChoiceBox<String> attachmentChoice;
    public TextField to;
    public TextField subject;
    public TextField carboncopy;
    public TextField blindcc;
    public TextArea emailContent;
    public static AnchorPane parent;

    //slide down animation to go back to emailcontroller
    @FXML
    private void goBack(ActionEvent event) {
        try {
            Scene currentScene = attachButton.getScene();
            StackPane pc = (StackPane) currentScene.getRoot();

            EmailController.root.translateYProperty().set(0);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(EmailController.root.translateYProperty(), currentScene.getHeight(), Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(e -> pc.getChildren().remove(EmailController.root));
            tim.play();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //makes sure we have at least one recipient
    private boolean hasARecipient(String recip) {
        String[] recips = recip.split(",");

        for (String email: recips) {
            if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
                    "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])" +
                    "|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-" +
                    "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
                return false;
        }

        return true;
    }

    //tells us if the requested ccs or bccs are valid
    private boolean validCarbonCopies(String cc) {
        String[] ccs = cc.split(",");

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

    //main driver method for StraightShot, sending emails
    @FXML
    private void sendEmail(ActionEvent e) {
        try {
            //first get all the text from the fields
            String recipients = to.getText().trim();
            String carbonCopies = carboncopy.getText().trim();
            String blindCC = blindcc.getText().trim();
            String subjectText = subject.getText().trim();
            String content = emailContent.getText().trim();

            //now validate all the user input
            if (!hasARecipient(recipients) || recipients.trim().length() == 0) {
                showPopupMessage("Please check your recipients email addresses", Main.primaryStage);
            }

            else if (!validCarbonCopies(carbonCopies) && carbonCopies.trim().length() > 0) {
                showPopupMessage("Please check your cc emails", Main.primaryStage);
            }

            else if (!validCarbonCopies(blindCC) && blindCC.trim().length() > 0) {
                showPopupMessage("Please check your bcc emails", Main.primaryStage);
            }

            //it's valid, yay!
            else {
                //we can send an email with no subject or message body but we should inform the user to make sure it is intended
                if (subjectText.length() == 0) {
                    showPopupMessage("Please note there is no subject", Main.primaryStage);
                }

                if (content.length() == 0) {
                    showPopupMessage("Please note there is no message body", Main.primaryStage);
                }

                //init email and password
                String ourEmail = MainController.emailAddress;
                StringBuilder passwordBuilder = new StringBuilder();
                for (int i = 0; i < MainController.password.length ; i++)
                    passwordBuilder.append(MainController.password[i]);

                //init tls and smtp
                Properties props = new Properties();
                props.put("mail.smtp.auth", true);
                props.put("mail.smtp.starttls.enable", true);
                props.put("mail.smtp.host", getEmailHost(ourEmail));
                props.put("mail.smtp.port", 587);

                //create session
                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(ourEmail, passwordBuilder.toString()); }
                });

                //make a message
                Message mes = new MimeMessage(session);
                mes.setFrom(new InternetAddress(ourEmail));

                //add recipients, carbon copies, and blind carbon copies to our email
                InternetAddress[] addresses = InternetAddress.parse(recipients);
                mes.addRecipients(Message.RecipientType.TO, addresses);

                InternetAddress[] ccAddresses = InternetAddress.parse(carbonCopies);
                if (ccAddresses.length > 0) {
                    mes.addRecipients(Message.RecipientType.CC, ccAddresses);
                }

                InternetAddress[] bccAddresses = InternetAddress.parse(blindCC);
                if (bccAddresses.length > 0) {
                    mes.addRecipients(Message.RecipientType.BCC, bccAddresses);
                }

                //set subject, content, and body
                mes.setSubject(subjectText);

                Multipart emailContent = new MimeMultipart();

                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setContent(content,"text/html");

                emailContent.addBodyPart(textBodyPart);

                if (attachmentsSize() >= 25) {
                    showPopupMessage("Sorry, but your attachments exceed the limit of 25MB. " +
                            "Please remove some files. Currently at " + String.format("%.2f", attachmentsSize()) + "MB", Main.primaryStage);
                }

                else {
                    //add attachments to our Multipart
                    addAttachments(emailContent);

                    mes.setContent(emailContent);

                    //attempt to send and if successful, inform user and go back to inbox screen
                    Transport.send(mes);

                    showPopupMessage("Sent email", Main.primaryStage);

                    goBack(null);
                }
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //calculate size of attachments in MB
    private double attachmentsSize() {
        double megaBytes = 0;

        for (File attachment : attachments)
            megaBytes += attachment.length() / 1024.0 / 1024.0;

        return megaBytes;
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

    //resolves email hosts if supported
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

    //this method adds attachments to the actual email that we are going to send
    private void addAttachments(Multipart multipart) {
        try {
            if (attachments != null && !attachments.isEmpty()) {
                for (File file : attachments) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    attachment.attachFile(file);
                    multipart.addBodyPart(attachment);
                }
            }
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //add files to the attachment list and the table, not the multipart yet
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
                    if (!attachments.contains(f)) {
                        attachments.add(f);
                    }
                }

                attachmentChoice.getItems().clear();
                attachmentsDisplay.clear();

                for (File attachment : attachments) {
                    String currentAttachDisp = attachment.getName() + " - " + getDisplayFileSize(attachment);
                    attachmentsDisplay.add(currentAttachDisp);
                }

                attachmentChoice.setItems(attachmentsDisplay);
                attachmentChoice.getSelectionModel().select(0);
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void removeFiles(ActionEvent e) {
        int index = attachmentChoice.getSelectionModel().getSelectedIndex();

        if (index >= 0 && index < attachmentsDisplay.size()) {
            attachmentsDisplay.remove(index);
            attachments.remove(index);

            attachmentChoice.setItems(attachmentsDisplay);

            if (attachmentsDisplay.size() > 0)
                attachmentChoice.getSelectionModel().select(0);
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

    //popup messages, can customize look based on the style sheet selected
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

    public TextField getTo() {
        return to;
    }

    public TextField getSubject() {
        return subject;
    }

    public TextArea getEmailContent() {
        return emailContent;
    }
}