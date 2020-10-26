import javafx.animation.*;
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
import javax.mail.Message;
import javax.mail.Store;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class ViewController  {

    //gui elements
    private Button backButton;
    public static AnchorPane parent;
    public Label fromLabel;
    private Label subjectLabel;
    private Label dateLabel;
    public TextArea emailContent;
    public Button replyButton;
    public Button forwardButton;
    public Button loadAttachments;
    public Button downloadAttachment;
    public ChoiceBox<String> attachmentsChoice;

    private Message localDisplayMessage;
    private EmailPreview thisPrev;

    private LinkedList<File> attachments;
    public ObservableList attachmentsDisplay = FXCollections.observableArrayList();

    private Store store;
    private Folder emailFolder;

    private void loadAttachments(ActionEvent e) {
        Main.startWorking("Loading...");
        //todo does this work? Either way do it in the background
        new Thread(this::initAttachments).start();
    }

    //todo does this work
    private void downloadAttachment(ActionEvent e) {
        try {

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
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //goto forward email screen
    @FXML
    private void forwardEmail() {
        try {
            Main.startWorking("Preparing forward");

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
    }

    //goto reply email screen
    @FXML
    private void replyEmail() {
        try {
            Main.startWorking("Preparing reply");

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

    private void initAttachments() {
        try {
            //todo load attachments from the email if it has any, inform if none
            //todo add attchments to the choicebox
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
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
