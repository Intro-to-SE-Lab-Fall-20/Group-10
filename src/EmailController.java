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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;

public class EmailController {

    @FXML
    public Label inboxLabel;
    public TableView table;
    public TableColumn message;
    public TableColumn from;
    public TableColumn date;
    public TableColumn subject;


    @FXML
    public void initialize() {
        try {
            //throwing excpetions is a dangerous practice :) you should always catch them and print the stack trace or do something else

            inboxLabel.setText("Viewing inbox of: " + getEmailAddress());
            fetchEmail();

            TableColumn fromCol = new TableColumn("From");
            TableColumn dateCol = new TableColumn("Date");
            TableColumn subjectCol = new TableColumn("Subject");
            TableColumn messageCol = new TableColumn("Message");

            table.getItems().addAll(fromCol, dateCol, subjectCol, messageCol);

            from.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("from"));
            date.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("date"));
            subject.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("subject"));
            message.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("message"));
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEmailAddress() {
        return Controller.emailAddress;
    }

    @FXML
    public static AnchorPane parent;
    @FXML
    public Button composeButton;
    @FXML
    public Button logoutButton;

    @FXML
    public Label unreadEmailsLabel;

    @FXML
    private void fetchEmail() {
        try {
            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < Controller.password.length; i++)
                passwordBuilder.append(Controller.password[i]);

            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", getEmailHost(getEmailAddress()));
            props.put("mail.smtp.port", 587);

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(getEmailAddress(), passwordBuilder.toString());
                        }
            });

            Store store = session.getStore("imaps");
            store.connect(getEmailHost(getEmailAddress()), getEmailAddress(), passwordBuilder.toString());

            //todo if we want to we can add a choice box for trash/inbox/sent/etc. then call refresh if they update it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Message[] messages = emailFolder.getMessages();

            unreadEmailsLabel.setText(messages.length != 1 ? messages.length + " unread emails" : " 1 unread email");

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];

                System.out.println("\n\nMessage " + i);
                System.out.println("---------------------------------");
                System.out.println("from: " + Arrays.toString(message.getFrom()));
                System.out.println("subject: " + message.getSubject());
                System.out.println("Receive date: " + message.getReceivedDate());
                System.out.println("Sent date: " + message.getSentDate());
                System.out.println("All recipients: " + Arrays.toString(message.getAllRecipients()));
                System.out.println("From folder: " + message.getFolder());

                String body = getMessageText(message);
                System.out.println("Body: " + body);

                writePart(Arrays.toString(message.getFrom()), message.getReceivedDate().toString(), message.getSubject(), body.substring(0, Math.min(body.length(), 40)));
            }

            emailFolder.close(false);
            store.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //todo if file from email is .mp3, display song length, if picture, display preview and maybe pixel size: nathan

    private String getMessageText(Message message) {
        try {
            String result = "";

            if (message.isMimeType("text/plain"))
                result = message.getContent().toString();

            else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();

                result = MMText(mimeMultipart);
            }

            return result;
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String MMText(MimeMultipart mimeMulti)  {
        try {
            StringBuilder result = new StringBuilder();

            int count = mimeMulti.getCount();

            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMulti.getBodyPart(i);

                if (bodyPart.isMimeType("text/plain")) {
                    result.append("\n").append(bodyPart.getContent());

                    break; //this is necessary, do not remove
                }

                else if (bodyPart.getContent() instanceof MimeMultipart)
                    result.append(MMText((MimeMultipart) bodyPart.getContent()));
            }

            return result.toString();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //todo add emails to table so that we can click on them and view the full message
    //display email messages in content table
    private void writePart(String from, String date, String subject, String message) {
        EmailPreview addMe = new EmailPreview(from,date,subject,message);
        table.getItems().add(addMe);
        table.refresh();
    }

    @FXML
    private void gotoCompose(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("compose.fxml"));
            Scene currentScene = logoutButton.getScene();
            root.translateYProperty().set(currentScene.getHeight());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene currentScene = logoutButton.getScene();
            root.translateXProperty().set(-currentScene.getWidth());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
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