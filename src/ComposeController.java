import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;

public class ComposeController {

    private List<File> attachements;

    @FXML
    public Button attachButton;

    @FXML
    public TextField to;
    @FXML
    public TextField subject;
    @FXML
    public TextField carboncopy;
    @FXML
    public TextField blindcc;
    @FXML
    public TextArea emailContent;

    @FXML
    public void initialize() {
        //stuff you need on startup of compose page
    }

    @FXML
    public static AnchorPane parent;

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("email.fxml"));
            Scene currentScene = attachButton.getScene();
            root.translateYProperty().set(-currentScene.getHeight());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void sendEmail(ActionEvent e) {
        try {
            String recipients = to.getText().trim();
            String carbonCopies = carboncopy.getText().trim();
            String blindCC = blindcc.getText().trim();
            String subjectText = subject.getText().trim();
            String content = emailContent.getText().trim();

            if (!hasARecipient(recipients) || recipients.trim().length() == 0) {
                showPopupMessage("Please check your recipients email addresses", Main.primaryStage);
            }

            else if (!validCarbonCopies(carbonCopies) && carbonCopies.trim().length() > 0) {
                showPopupMessage("Please check your cc emails", Main.primaryStage);
            }

            else if (!validCarbonCopies(blindCC) && blindCC.trim().length() > 0) {
                showPopupMessage("Please check your bcc emails", Main.primaryStage);
            }

            else {
                if (subjectText.length() == 0) {
                    showPopupMessage("Please note there is no subject", Main.primaryStage);
                }

                if (content.length() == 0) {
                    showPopupMessage("Please note there is no message body", Main.primaryStage);
                }

                String ourEmail = Controller.emailAddress;
                StringBuilder passwordBuilder = new StringBuilder();
                for (int i = 0 ; i < Controller.password.length ; i++)
                    passwordBuilder.append(Controller.password[i]);

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

                Message mes = new MimeMessage(session);
                mes.setFrom(new InternetAddress(ourEmail));

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

                mes.setSubject(subjectText);

                Multipart emailContent = new MimeMultipart();

                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText(content);

                emailContent.addBodyPart(textBodyPart);
                addAttachements(emailContent);

                mes.setContent(emailContent);

                Transport.send(mes);

                showPopupMessage("Sent messaage", Main.primaryStage);

                goBack(null);
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //must pass in a file that holds audio and this will return min:sec format of song length
    private String getSongLength(File f) {
        try {
            //todo return the Minutes:Seconds length of an mp3 or wav audio file
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //must pass in a file that is an iamge and will return [xDim, yDim] of the image
    private int[] getImageDimensions(File imageFile) {
        try {
            return new int[]{ImageIO.read(imageFile).getWidth(), ImageIO.read(imageFile).getHeight()};
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return new int[]{0,0};
    }

    //will return txt, png, mp3, or whatever the file type is
    private String getFileExtension(File f) {
        return f.getName().replace(f.getName().substring(0, f.getName().lastIndexOf('.')),"").replace(".","");
    }

    //returns a representation if a file in MB or KB with 2 decimal places
    private String getDisplayFileSize(File f) {
        DecimalFormat threeDecimal = new DecimalFormat("#.###");
        double mbSize = f.length() / 1024.0 / 1024.0;

        if (mbSize < 1)
            return threeDecimal.format((mbSize * 1024.0)) + " KB";
        else
            return threeDecimal.format(mbSize) + " MB";
    }

    private void addAttachements(Multipart multipart) {
        try {
            if (attachements != null && !attachements.isEmpty()) {
                for (File file : attachements) {
                    MimeBodyPart attachement = new MimeBodyPart();
                    attachement.attachFile(file);
                    multipart.addBodyPart(attachement);
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //todo you need to test yahoho and outlook emails still
    private String getEmailHost(String email) throws Exception {
        if (email.endsWith("gmail.com"))
            return "smtp.gmail.com";
        else if (email.endsWith("yahoo.com"))
            return "smtp.mail.yahoo.com";
        else if (email.endsWith("outlook.com"))
            return "smtp.office365.com";
        else
            throw new Exception("Unsupported email host");
    }

    //todo display in a pane the attachements where you can select it and delete it or replace it (name, size, type): nathan
    //todo for pictures show dimensions, for mp3 files show song length
    @FXML
    private void addFiles(ActionEvent e) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Add Attachements");
            attachements = fc.showOpenMultipleDialog(null);

            if (attachements != null) {
                StringBuilder build = new StringBuilder();

                for (File attachement : attachements) {
                    System.out.println(getImageDimensions(attachement)[0] + "," + getImageDimensions(attachement)[1]);
                    build.append(attachement.getName()).append(" ").append((int) Math.ceil(attachement.length() / 1024.0) / 1024.0).append("MB").append("\n");
                }

                attachButton.setTooltip(new Tooltip(build.toString()));
            }

            else
                System.out.println("Invalid file");
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        //here you can save user stuff like theme and what not: mallory
        FileWriter file;
        try {
            file = new FileWriter("user.txt");
            file.write("");
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

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
            popup.setY(stage.getY() + 25);
        });
        popup.show(stage);
    }
}