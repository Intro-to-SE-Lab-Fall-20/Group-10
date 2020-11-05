import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class NoteViewController {

    @FXML public TextField noteNameField;
    @FXML public TextArea noteTextArea;
    @FXML public Button saveAndResign;
    @FXML public Button discardChanges;
    @FXML public Button deleteNote;

    @FXML
    private void initialize() {
        noteTextArea.setText(NoteMainController.currentContents);
        noteNameField.setText(NoteMainController.currentName);
    }

    @FXML
    private void saveAndResign (ActionEvent e) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(NoteMainController.currentFile,false));
            br.write(noteTextArea.getText());
            br.flush();
            br.close();

            NoteMainController.currentFile.renameTo(
                    new File(NoteMainController.currentFile.getAbsolutePath().replace(NoteMainController.currentFile.getName(), noteNameField.getText())));

            NoteMainController.refreshTable();

            Scene currentScene = noteTextArea.getScene();
            StackPane pc = (StackPane) currentScene.getRoot();
            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(MasterMainController.root.translateYProperty(),currentScene.getHeight() , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void discardChanges (ActionEvent e) {
        try {
            Scene currentScene = noteTextArea.getScene();
            StackPane pc = (StackPane) currentScene.getRoot();
            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(MasterMainController.root.translateYProperty(),currentScene.getHeight() , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @FXML
    private void deleteNote (ActionEvent e) {
        try {
            NoteMainController.currentFile.delete();

            NoteMainController.refreshTable();

            Scene currentScene = noteTextArea.getScene();
            StackPane pc = (StackPane) currentScene.getRoot();
            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(MasterMainController.root.translateYProperty(),currentScene.getHeight() , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.play();

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
}
