import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;


public class EmailController {

    //used for our inbox emailTable, will add more stuff
    @FXML
    public Label inboxLabel;
    public TableView table;
    public TableColumn message;
    public TableColumn from;
    public TableColumn date;
    public TableColumn subject;
    public static AnchorPane parent;
    public Button composeButton;
    public Button logoutButton;
    public Button deleteButton;
    public Label unreadEmailsLabel;
    public CheckBox hideOnCloseCheckBox;
    public ChoiceBox<String> folderChoiceBox;

    //current email folder
    private Message[] messages;
    private String currentFolder = "INBOX";

    public ObservableList folderList = FXCollections.observableArrayList();

    @FXML
    public void toggleHideOnClose() {}

    @FXML
    public void initialize() {
        try {
            //update the email address label
            inboxLabel.setText("Viewing inbox of: " + getEmailAddress());

            //init emailTable column names
            TableColumn fromCol = new TableColumn("From");
            TableColumn dateCol = new TableColumn("Date");
            TableColumn subjectCol = new TableColumn("Subject");
            TableColumn messageCol = new TableColumn("Message");

            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            //set how each column will display its data <EmailPreview, String> means display this object as a string
            from.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("from"));
            date.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("date"));
            subject.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("subject"));
            message.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("message"));

            //don't let the user rearrange the column ordering
            table.getColumns().addListener((ListChangeListener) change -> {
                change.next();
                if(change.wasReplaced()) {
                    table.getColumns().clear();
                    table.getColumns().addAll(from,date,subject,message);
                }
            });

            //todo check for new emails every 30 seconds or so
            //todo forward and reply should be disabled unless a tablerow (email) is focused
            //todo search for emails or subjects containing whats in searchbox, if null or "" display everything in folder
            //todo for pictures show dimensions, for mp3 files show song length
            //todo update currentfolder when user interacts with it

            table.setRowFactory( tv -> {
                TableRow<EmailPreview> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                        EmailPreview rowData = row.getItem();
                        //TODO open up displayer (similar to compose) displays the message [back, delete, foward, reply]
                    }
                });
                return row ;
            });

            initFolders();
            fetchEmail("INBOX");

            folderChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, n1, n2) -> {
                String folder = folderChoiceBox.getItems().get((Integer) ov.getValue());
                fetchEmail(folder);
            });
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEmailAddress() {
        return Controller.emailAddress;
    }

    //initialize folders choicebox for email address
    private void initFolders() {
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

            Folder[] f = store.getDefaultFolder().list();
            folderList.clear();

            ArrayList<String> folders = new ArrayList<>();

            for (Folder fd:f) {
                folders.add(fd.getName());
            }

            folderList.clear();
            folderList.addAll(folders);
            folderChoiceBox.getItems().addAll(folderList);
            folderChoiceBox.getSelectionModel().select(0);

            folderChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov,
                                                                                     Number old_val, Number new_val) -> currentFolder = String.valueOf(new_val));
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //gets all emails from the validated folder, adds them to the emailTable, and prints to console
    @FXML
    private void fetchEmail(String loadFolder) {
        try {
            //first convert char[] holding password to a string builder that we can call toString on
            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < Controller.password.length; i++)
                passwordBuilder.append(Controller.password[i]);

            //init properties for smtp (Simple Mail Transfer Protocol)
            Properties props = new Properties();
            props.put("mail.smtp.auth", true); //we should authenticate
            props.put("mail.smtp.starttls.enable", true); //(tls is transport layer security and is a good practice)
            props.put("mail.smtp.host", getEmailHost(getEmailAddress())); //set host
            props.put("mail.smtp.port", 587); //port 587 is the standard SMTP port

            //create a session from our email (this is from our javaMail library)
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(getEmailAddress(), passwordBuilder.toString());
                        }
            });

            //imaps is simply a protocol (Internet Message Access Protocol)
            //create an imaps session using our emailAddress host, email, and password
            Store store = session.getStore("imaps");
            store.connect(getEmailHost(getEmailAddress()), getEmailAddress(), passwordBuilder.toString());

            //get all the messages from the specified folder
            Folder emailFolder = store.getFolder(loadFolder);
            emailFolder.open(Folder.READ_ONLY);
            messages = emailFolder.getMessages();

            //set how many emails we found
            unreadEmailsLabel.setText(messages.length != 1 ? messages.length + " emails" : " 1 email");

            table.getItems().clear();

            //Add emails to table
            for (int i = messages.length - 1; i >= 0 ; i--) {
                Message message = messages[i];
                String body = getMessageText(message);
                writePart(Arrays.toString(message.getFrom()), message.getReceivedDate().toString(), message.getSubject(), body);
            }

            //good practice to close the folder and javax.mail.store
            emailFolder.close();
            store.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get the body from a Message
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

    //gets the email body of a MimeMultipart message
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

    //display email messages in content emailTable
    private void writePart(String from, String date, String subject, String message) {
        table.getItems().add(new EmailPreview(from,date,subject,message));
        table.refresh();
    }

    //animation for going to compose screen
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
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void gotoReply(ActionEvent event) {
        try {
            System.out.println("TODO");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void gotoForward(ActionEvent event) {
        try {
            System.out.println("TODO");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //animate the sliding away Inbox scene back to the main menu
    @FXML
    private void goBack(ActionEvent event) {
        try {
            //load new parent and scene
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene currentScene = logoutButton.getScene();
            root.translateXProperty().set(-currentScene.getWidth()); //new scene starts off current scene

            //add the new scene
            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            //animate the scene in
            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);

            //play animation and when done, remove the old scene
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //would like to do a minimizing animation but might not have time
    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    //a general switch statement that only allows our supported email addresses
    //if we can't get yahoo or outlook working, just remove those cases
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

    //delete an email and refresh the table (from main screen)
    @FXML
    public void deleteEmail() {
        try {
            int deleteIndex = messages.length - table.getSelectionModel().getSelectedIndex() - 1;

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

            Folder emailFolder = store.getFolder(currentFolder);
            emailFolder.open(Folder.READ_WRITE);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            messages = emailFolder.getMessages();

            messages[deleteIndex].setFlag(Flags.Flag.DELETED, true);

            emailFolder.close(true);
            store.close();

            fetchEmail(currentFolder);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void close_app(MouseEvent e) {
        FileWriter file;
        try {
            file = new FileWriter("user.txt");
            file.write("");
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (hideOnCloseCheckBox.isSelected())
            Main.primaryStage.setIconified(true);
        else
            System.exit(0);
    }
}