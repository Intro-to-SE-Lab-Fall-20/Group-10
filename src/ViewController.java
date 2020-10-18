import javafx.animation.*;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.LinkedList;

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
    private Label dateLabel;
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
        try {
            subjectLabel.setText("Subject: " + EmailController.currentMessageSubject);
            fromLabel.setText("From: " + EmailController.currentMessageFrom);
            dateLabel.setText("Date: " + EmailController.currentMessageDate);
            emailContent.setText(EmailController.currentMessageBody);

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

            initAttachments();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void forwardEmail() {
        try {
            Parent root = FXMLLoader.load(EmailController.class.getResource("forward.fxml"));
            Scene currentScene = backButton.getScene();
            root.translateXProperty().set(currentScene.getWidth());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);

            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void replyEmail() {
        try {
            Parent root = FXMLLoader.load(EmailController.class.getResource("reply.fxml"));
            Scene currentScene = backButton.getScene();
            root.translateXProperty().set(currentScene.getWidth());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);

            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
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
        clearLocalAttachments();

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
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    public static void clearLocalAttachments() {
        try {
            File dir = new File("tmp");
            rmDir(dir);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            clearLocalAttachments();

            try {
                Parent root = FXMLLoader.load(getClass().getResource("email.fxml"));
                Scene currentScene = backButton.getScene();

                StackPane pc = (StackPane) currentScene.getRoot();
                pc.getChildren().add(root);
                pc.getChildren().remove(currentScene);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void rmDir(File f) {
        if (f.isDirectory()) {
            for (File file: f.listFiles())
                rmDir(file);

            f.delete();
        }

        else {
            f.delete();
        }
    }

    private void initAttachments() {
        try {
            for (File attachment : EmailController.currentMessageAttachments) {
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
