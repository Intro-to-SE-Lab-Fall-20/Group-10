import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            System.out.println(EmailController.currentMessageSubject);
            System.out.println(EmailController.currentMessageFrom);
            System.out.println(EmailController.currentMessageDate);
            System.out.println(EmailController.currentMessageBody);
            System.out.println("num attachments: " + EmailController.currentMessageAttachments.size());

            for (File f: EmailController.currentMessageAttachments)
                System.out.println(f.getAbsolutePath());

            //todo now all information is here and available. Now display it.

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
                            //todo copy file to location specified by FileChooser
                        }

                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                return row ;
            });
        }

        catch (Exception e) {
            e.printStackTrace();
        }
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

        File dir = new File("sstemp");
        rmDir(dir);

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
            File dir = new File("sstemp");
            rmDir(dir);

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

    private void rmDir(File f) {
        if (f.isDirectory()) {
            for (File file: f.listFiles())
                rmDir(file);

            f.delete();
        }

        else {
            f.delete();
        }
    }

    //used to add attachment representations to the table
    private void addAttachmentsToTable(String name, String size, String type, File file) {

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
