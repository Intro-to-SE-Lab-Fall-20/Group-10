import javafx.animation.*;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class ForwardController {

    //gui elements
    @FXML
    public static AnchorPane parent;
    @FXML
    private TextField forwardTo;
    @FXML
    private TextField forwardSubject;
    @FXML
    private TextArea emailContent;
    @FXML
    private TableView<AttachmentPreview> table;
    @FXML
    private TableColumn name;
    @FXML
    private TableColumn size;
    @FXML
    private TableColumn type;
    @FXML
    private Button attachButton;
    @FXML
    private Button discardButton;
    @FXML
    private Button forwardButton;

    //attachments list
    private LinkedList<File> additionalAttachments = EmailController.currentMessageAttachments;

    @FXML
    public void initialize() {
        try {
            forwardSubject.setText("");
            forwardTo.setText("");
            emailContent.setText(EmailController.currentMessageBody);

            //set how each column will display its data <AttachmentPreview, String> means display this object as a string
            name.setCellValueFactory(new PropertyValueFactory<AttachmentPreview, String>("name"));
            size.setCellValueFactory(new PropertyValueFactory<AttachmentPreview, String>("size"));
            type.setCellValueFactory(new PropertyValueFactory<AttachmentPreview, String>("type"));

            table.setColumnResizePolicy((param) -> true );

            //don't let the user rearrange the columns
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

            //you can still download an attachment when forwarding it
            table.setRowFactory( tv -> {
                TableRow<AttachmentPreview> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                        try {
                            File copyMe = row.getItem().getPointerFile().getAbsoluteFile();
                            Path from = Paths.get(copyMe.toURI());

                            DirectoryChooser directoryChooser = new DirectoryChooser();
                            directoryChooser.setTitle("Select location to download " + copyMe.getName() + " to");
                            File selectedDirectory = directoryChooser.showDialog(Main.primaryStage);

                            if (selectedDirectory == null)
                                return;

                            Path to = Paths.get((selectedDirectory + System.getProperty("file.separator") + copyMe.getName()));

                            if (selectedDirectory != null && selectedDirectory.isDirectory())
                                Files.copy(from, to);

                            showPopupMessage("Successfully saved " + copyMe.getName() + " to " + selectedDirectory.getName(), Main.primaryStage);

                        }

                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return row ;
            });

            //remove an attachment
            table.setOnKeyPressed(keyEvent -> {
                AttachmentPreview selectedItem = table.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    if (keyEvent.getCode().equals(KeyCode.DELETE) || keyEvent.getCode().equals(KeyCode.BACK_SPACE)){
                        additionalAttachments.remove(0);
                        table.getItems().remove(table.getSelectionModel().getSelectedIndex());
                        table.refresh();
                    }
                }
            });

            initAttachments();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //send the forward
    @FXML
    private void sendForward(ActionEvent event) {
        String forwardingSubject = forwardSubject.getText();
        String forwardingRecipients = forwardTo.getText();
        String emailCont = emailContent.getText();

        if (!validEmailAddr(forwardingRecipients)) {
            showPopupMessage("Please check your recipients email addresses", Main.primaryStage);
        }

        else {
            try {
                if (forwardingSubject.length() == 0) {
                    showPopupMessage("Please note there is no subject", Main.primaryStage);
                }

                if (emailCont.length() == 0) {
                    showPopupMessage("Please note there is no message body", Main.primaryStage);
                }

                //init email and password
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

                Message message = EmailController.currentMessage;
                Message forwardingMessage = message.reply(false);

                forwardingMessage.setSubject(forwardingSubject);

                InternetAddress[] addresses = InternetAddress.parse(forwardingRecipients);
                forwardingMessage.addRecipients(Message.RecipientType.TO, addresses);

                Multipart emailContent = new MimeMultipart();

                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText(emailCont);

                emailContent.addBodyPart(textBodyPart);

                if (attachmentsSize() >= 25) {
                    showPopupMessage("Sorry, but your attachments exceed the limit of 25MB. " +
                            "Please remove some files. Currently at " + String.format("%.2f", attachmentsSize()) + "MB", Main.primaryStage);
                }

                else {
                    addAttachments(emailContent);
                    forwardingMessage.setContent(emailContent);
                    Transport.send(forwardingMessage);
                    showPopupMessage("Email forwarded successfully", Main.primaryStage);
                    goBack(null);
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //attachments total size in MB
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

    //add files to list and tableview
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
                    if (!additionalAttachments.contains(f)) {
                        additionalAttachments.add(f);
                    }
                }

                table.getItems().clear();

                for (File attachment : additionalAttachments) {
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
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            ViewController.clearLocalAttachments();

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

    //fill tableview with attachment names
    private void initAttachments() {
        try {
            for (File attachment : additionalAttachments) {
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

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //used to add attachment representations to the table
    private void addAttachmentsToTable(String name, String size, String type, File file) {
        table.getItems().add(new AttachmentPreview(name,size,type,file));
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

    private String getEmailAddress() {
        return Controller.emailAddress;
    }

    //make sure that we support the email server
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
            popup.setY(stage.getY() + 25);
        });
        popup.show(stage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }
}
