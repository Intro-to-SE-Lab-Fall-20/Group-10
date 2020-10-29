import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.mail.Folder;
import javax.mail.Store;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class ViewController  {

    //todo when going to reply or forward, pass it OUR attachments and override whatever is there if of
    // course there are files inside of view right now that have also been loaded

    //todo before going to reply or forward when we load attachments in this file, show those in our choicebox here too

    //gui elements
    @FXML private Button backButton;
    @FXML public static AnchorPane parent;
    @FXML public Label fromLabel;
    @FXML private Label subjectLabel;
    @FXML private Label dateLabel;
    @FXML public TextArea emailContent;
    @FXML public Button replyButton;
    @FXML public Button forwardButton;
    @FXML public Button loadAttachments;
    @FXML public Button downloadAttachment;
    @FXML public ChoiceBox<String> attachmentsChoice;

    public static LinkedList<File> attachments;
    public static ObservableList attachmentsDisplay = FXCollections.observableArrayList();

    private Store store;
    private Folder emailFolder;

    public Thread loadingAttachThread;

    @FXML
    private void loadAttachments(ActionEvent e) {
        Main.startWorking("Loading...",0);

        loadAttachments.setDisable(true);
        forwardButton.setDisable(true);
        replyButton.setDisable(true);
        downloadAttachment.setDisable(true);
        backButton.setDisable(true);

        loadingAttachThread = new Thread(() -> {
            EmailController.initAttachments();

            if (EmailController.currentMessageAttachments.size() == 0) {
                Main.startWorking("No attachments!", 1000);
                Platform.runLater(() -> loadAttachments.setDisable(false));
                return;
            }

            attachments = EmailController.currentMessageAttachments;

            attachmentsChoice.getItems().clear();
            attachmentsDisplay.clear();

            for (File attachment : attachments) {
                String currentAttachDisp = attachment.getName() + " - " + getDisplayFileSize(attachment);
                attachmentsDisplay.add(currentAttachDisp);
            }

            attachmentsChoice.setItems(attachmentsDisplay);

            Platform.runLater(() -> attachmentsChoice.getSelectionModel().select(0));

            Main.startWorking("Loaded!",2500);

            Platform.runLater(() -> {
                loadAttachments.setDisable(false);
                forwardButton.setDisable(false);
                replyButton.setDisable(false);
                downloadAttachment.setDisable(false);
                backButton.setDisable(false);
            });
        });

        loadingAttachThread.start();
    }

    @FXML
    private void downloadAttachment(ActionEvent e) {
        try {
            if (EmailController.currentMessageAttachments == null || EmailController.currentMessageAttachments.size() == 0)
                return;

            int index = attachmentsChoice.getSelectionModel().getSelectedIndex();

            if (index >= 0 && index < attachments.size()) {
                File chosen = attachments.get(index);
                Path from = Paths.get(chosen.toURI());

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select location to download " + chosen.getName() + " to");
                File selectedDirectory = directoryChooser.showDialog(Main.primaryStage);

                if (selectedDirectory == null)
                    return;

                Path to = Paths.get((selectedDirectory + System.getProperty("file.separator") + chosen.getName()));


                if (selectedDirectory != null && selectedDirectory.isDirectory())
                    Files.copy(from, to);

                showPopupMessage("Successfully saved " + chosen.getName() + " to " + selectedDirectory.getName(), Main.primaryStage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //prepare fields with email contents
    @FXML
    public void initialize() {
        try {
            subjectLabel.setText("Subject: " + EmailController.currentMessageSubject);
            fromLabel.setText("From: " + EmailController.currentMessageFrom);
            dateLabel.setText("Date: " + EmailController.currentMessageDate);
            emailContent.setText(EmailController.currentMessageBody);
            EmailController.currentMessageAttachments = null;
            attachments = null;
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //goto forward email screen
    @FXML
    private void forwardEmail() {
        try {
            loadAttachments.setDisable(true);
            forwardButton.setDisable(true);
            replyButton.setDisable(true);
            downloadAttachment.setDisable(true);
            backButton.setDisable(true);

            new Thread(() -> {
                Main.startWorking("Preparing forward...",0);

                EmailController.initAttachments();

                Main.startWorking("Prepared!",1000);

                Platform.runLater(() -> {
                    try {
                        loadAttachments.setDisable(false);
                        forwardButton.setDisable(false);
                        replyButton.setDisable(false);
                        downloadAttachment.setDisable(false);
                        backButton.setDisable(false);
                        attachments = EmailController.currentMessageAttachments;

                        Parent root = FXMLLoader.load(EmailController.class.getResource("forward.fxml"));
                        EmailController.root = root;
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
                });
            }).start();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //goto reply email screen
    @FXML
    private void replyEmail() {
        try {
            loadAttachments.setDisable(true);
            forwardButton.setDisable(true);
            replyButton.setDisable(true);
            downloadAttachment.setDisable(true);
            backButton.setDisable(true);

            new Thread(() -> {
                Main.startWorking("Preparing reply...",0);

                EmailController.initAttachments();

                Main.startWorking("Prepared!",1000);

                Platform.runLater(() -> {
                    try {
                        loadAttachments.setDisable(false);
                        forwardButton.setDisable(false);
                        replyButton.setDisable(false);
                        downloadAttachment.setDisable(false);
                        backButton.setDisable(false);
                        attachments = EmailController.currentMessageAttachments;

                        Parent root = FXMLLoader.load(EmailController.class.getResource("reply.fxml"));
                        EmailController.root = root;
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
                });
            }).start();
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
            popup.setY(stage.getY() + 20);
        });
        popup.show(stage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    public static void clearLocalAttachments() {
        try {
            File dir = new File("temp");
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
            EmailController.currentMessageAttachments = null;

            Scene currentScene = forwardButton.getScene();
            StackPane pc = (StackPane) currentScene.getRoot();

            EmailController.root.translateYProperty().set(0);
            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(EmailController.root.translateYProperty(), currentScene.getHeight(), Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(e -> pc.getChildren().remove(EmailController.root));
            tim.play();

            EmailController.viewRoot.translateYProperty().set(0);
            Timeline tim1 = new Timeline();
            KeyValue kv1 = new KeyValue(EmailController.viewRoot.translateYProperty(), currentScene.getHeight(), Interpolator.EASE_IN);
            KeyFrame kf1 = new KeyFrame(Duration.seconds(0.5), kv1);
            tim1.getKeyFrames().add(kf1);
            tim1.setOnFinished(e -> pc.getChildren().remove(EmailController.viewRoot));
            tim1.play();
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

    //returns a representation if a file in MB or KB with 2 decimal places
    private static String getDisplayFileSize(File f) {
        DecimalFormat threeDecimal = new DecimalFormat("#.##");
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
