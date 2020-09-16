import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    @FXML
    private void sendEmail(ActionEvent e) {
        try {
            String recipients = to.getText();

            if (recipients.contains("@")) { //todo not the best way to make sure there is an email address
                String carbonCopies = carboncopy.getText();
                String blindCC = blindcc.getText();
                String subjectText = subject.getText();
                String content = emailContent.getText();
                String ourEmail = Controller.emailAddress;
                StringBuilder passwordBuilder = new StringBuilder();
                for (int i = 0 ; i < Controller.password.length ; i++)
                    passwordBuilder.append(Controller.password[i]);

                //todo inform if no subject line and confirm send email

                //todo add better input checking

                Properties props = new Properties();
                props.put("mail.smtp.auth", true);
                props.put("mail.smtp.starttls.enable", true);
                props.put("mail.smtp.host", getEmailHost(ourEmail));
                props.put("mail.smtp.port", 587);

                Session session = Session.getDefaultInstance(props,
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

                //todo inform of successful send of email and go back to main screen
                System.out.println("Sent messaage");
                goBack(null);
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
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

    @FXML
    private void addFiles(ActionEvent e) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Add Attachements");
            attachements = fc.showOpenMultipleDialog(null);

            if (attachements != null) {
                StringBuilder build = new StringBuilder();

                for (File attachement : attachements) {
                    build.append(attachement.getName()).append(" ").append((int) Math.ceil(attachement.length() / 1024.0) / 1024.0).append("MB").append("\n");
                }

                attachButton.setTooltip(new Tooltip(build.toString()));
            }

            else {
                System.out.println("Invalid file");
            }
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
}