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
import javax.mail.internet.MimeMessage;
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
            //throwing excpetions is a dangerous practice :)
            //you should always catch them and print the stack trace or do something else
            inboxLabel.setText("Viewing inbox of: " + getEmailAddress());
            fetchEmail();
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

                MimeMessage mimeMessage = (MimeMessage) message;
                BodyPart regularText = null;
                Multipart cont = (Multipart) mimeMessage.getContent();

                int len = cont.getCount();

                for (int j = 0 ; j < len ; j++) {
                    regularText = cont.getBodyPart(j);
                }

                String result = (String) regularText.getContent();

                System.out.println("Body: " + result);

                //preview 40 characters until we click on it I suppose
                writePart(Arrays.toString(message.getFrom()), message.getReceivedDate().toString(), message.getSubject(), result.substring(0, Math.min(result.length(), 40)));
            }

            emailFolder.close(false);
            store.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //todo as you can see, it prints it to the console so we just need to display that in GUI form
    //display email messages in content table
    private void writePart(String from, String date, String subject, String message) {
        try {
            TableColumn fromCol = new TableColumn("From");
            fromCol.setCellValueFactory(new PropertyValueFactory<String, String>("from"));

            TableColumn dateCol = new TableColumn("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<String, String>("date"));

            TableColumn subjectCol = new TableColumn("Subject");
            subjectCol.setCellValueFactory(new PropertyValueFactory<String, String>("subject"));

            TableColumn messageCol = new TableColumn("Message");
            messageCol.setCellValueFactory(new PropertyValueFactory<String, String>("message"));


            table.getItems().addAll(fromCol, dateCol, subjectCol, messageCol);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
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