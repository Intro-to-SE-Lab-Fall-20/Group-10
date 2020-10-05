import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
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
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Properties;

public class ViewController  {

    @FXML
    private Button backButton;
    @FXML
    public static AnchorPane parent;
    @FXML
    public Label fromLabel;
    @FXML
    private Label subjectLabel;
    @FXML
    public TextArea emailContent;
    @FXML
    public Button replyButton;
    @FXML
    public Button forwardButton;

    @FXML
    public TableView table;
    public TableColumn name;
    public TableColumn size;
    public TableColumn type;

    private Message localDisplayMessage;
    private EmailPreview thisPrev;

    private LinkedList<File> attachments;

    private Store store;
    private Folder emailFolder;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            try {
                thisPrev = new EmailPreview("","","","");

                localDisplayMessage = EmailController.currentMessage;

                thisPrev.setDate(String.valueOf(localDisplayMessage.getSentDate()));
                thisPrev.setFrom(String.valueOf(localDisplayMessage.getFrom()[0]));
                thisPrev.setSubject(localDisplayMessage.getSubject());
                thisPrev.setMessage(getMessageText(localDisplayMessage));

                fromLabel.setText("From: " + thisPrev.getFrom());
                subjectLabel.setText("Subject: " + thisPrev.getSubject());
                emailContent.setText(thisPrev.getFullMessage());

                initAttachments();

                //todo add attachments to table, do not let them delete but let them download it
                //to add other attachements they will have to press forward or reply (these should open their own seperate FXML file
                // so that they can be called from emailcontroller)

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //set how each column will display its data <AttachmentPreview, String> means display this object as a string
        name.setCellValueFactory(new PropertyValueFactory<AttachmentPreview, String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<AttachmentPreview, String>("size"));
        type.setCellValueFactory(new PropertyValueFactory<AttachmentPreview, String>("type"));

        table.setColumnResizePolicy((param) -> true );

        table.getColumns().addListener((ListChangeListener) change -> {
            change.next();
            if(change.wasReplaced()) {
                table.getColumns().clear();
                table.getColumns().addAll(name,size,type);
            }
        });

        name.setCellFactory(new Callback<TableColumn<AttachmentPreview,String>, TableCell<AttachmentPreview,String>>() {
            @Override
            public TableCell<AttachmentPreview, String> call(TableColumn<AttachmentPreview, String> param) {
                return new TableCell<>() {
                    private Text text;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item);
                            text.setWrappingWidth(80);
                            setGraphic(text);
                        }
                    }
                };
            }
        });

        size.setCellFactory(new Callback<TableColumn<AttachmentPreview,String>, TableCell<AttachmentPreview,String>>() {
            @Override
            public TableCell<AttachmentPreview, String> call(TableColumn<AttachmentPreview, String> param) {
                return new TableCell<>() {
                    private Text text;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item);
                            text.setWrappingWidth(80);
                            setGraphic(text);
                        }
                    }
                };
            }
        });

        type.setCellFactory(new Callback<TableColumn<AttachmentPreview,String>, TableCell<AttachmentPreview,String>>() {
            @Override
            public TableCell<AttachmentPreview, String> call(TableColumn<AttachmentPreview, String> param) {
                return new TableCell<>() {
                    private Text text;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item);
                            text.setWrappingWidth(80);
                            setGraphic(text);
                        }
                    }
                };
            }
        });

        table.setRowFactory( tv -> {
            TableRow<AttachmentPreview> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    try {
                        //todo download to location specified by FileChooser
                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return row ;
        });
    }

    //get the body from a Message
    public String getMessageText(Message message) {
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
            openContainers();

            StringBuilder result = new StringBuilder();

            int count = mimeMulti.getCount();

            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMulti.getBodyPart(i);

                if (bodyPart.isMimeType("text/plain")) {
                    result.append("\n").append(bodyPart.getContent()); //todo folder closed exception

                    break; //this is necessary, do not remove
                }

                else if (bodyPart.getContent() instanceof MimeMultipart)
                    result.append(MMText((MimeMultipart) bodyPart.getContent()));
            }

            closeContainers();

            return result.toString();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void forwardEmail() {
        //todo goto sep gui
        //https://www.tutorialspoint.com/javamail_api/javamail_api_forwarding_emails.htm
    }

    private void replyEmail() {
        //todo goto sep gui
        //https://www.tutorialspoint.com/javamail_api/javamail_api_replying_emails.htm
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
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

        //todo delete temp file attachments

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
            popup.setY(stage.getY() + 25);
        });
        popup.show(stage);
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            //todo delete temp attachments folder

            Parent root = FXMLLoader.load(getClass().getResource("email.fxml"));
            Scene currentScene = backButton.getScene();
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

    //used to add attachment representations to the table
    private void addAttachmentsToTable(String name, String size, String type, File file) {
        table.getItems().add(new AttachmentPreview(name,size,type, file));
        table.refresh();
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

    private void initAttachments() {
        try {
            openContainers();

            Multipart multiPart = (Multipart) localDisplayMessage.getContent();

            //we will store attachemnts inside of downloads for now until I figure out a better way
            //such as storing in AppData/temp and then delete on exit
            String home = System.getProperty("user.home");

            for (int i = 0; i < multiPart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    File file = new File(home + "/Downloads/" + part.getFileName());
                    openContainers();
                    part.saveFile(file);
                    attachments.add(file);
                    closeContainers();
                }
            }

            if (attachments != null) {
                for (File attachment : attachments) {
                    if (getFileExtension(attachment).equalsIgnoreCase("png") ||
                            getFileExtension(attachment).equalsIgnoreCase("jpg") ||
                            getFileExtension(attachment).equalsIgnoreCase("jpeg")) {
                        int[] dim = getImageDimensions(attachment);
                        addAttachmentsToTable(attachment.getName().replace("." + getFileExtension(attachment),""),
                                getDisplayFileSize(attachment),dim[0] + " x " + dim[1], attachment);
                    }

                    else {
                        addAttachmentsToTable(attachment.getName().replace("." + getFileExtension(attachment),""),
                                getDisplayFileSize(attachment),getFileExtension(attachment), attachment);
                    }
                }
            }

            closeContainers();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //will return txt, png, mp3, or whatever the file type is
    private String getFileExtension(File f) {
        String extension = "";
        int i = f.getName().lastIndexOf('.');

        if (i > 0) {
            extension = f.getName().substring(i + 1);
        }

        return extension.replace(".","");
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

    private void openContainers() {
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

            store = session.getStore("imaps");
            store.connect(getEmailHost(getEmailAddress()), getEmailAddress(), passwordBuilder.toString());

            //get all the messages from the specified folder
            emailFolder = store.getFolder(EmailController.currentFolderName);
            emailFolder.open(Folder.READ_ONLY);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEmailAddress() {
        return Controller.emailAddress;
    }

    private void closeContainers() {
        try {
            store.close();
            emailFolder.close();
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
            throw new IllegalAccessException("Unsupported email host");
    }
}
