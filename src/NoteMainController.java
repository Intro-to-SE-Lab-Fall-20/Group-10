import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;

public class NoteMainController {

    @FXML public TableView table;
    @FXML public TableColumn noteColumn;
    @FXML public Button addNote;
    @FXML public Button back;
    @FXML public Button openNote;
    @FXML public Button deleteNote;

    @FXML
    public void back(ActionEvent e) {
        //todo go back to screen to choose note or email
    }

    @FXML
    public void addNote(ActionEvent e) {
        //todo go to create note
    }

    @FXML
    public void openNote(ActionEvent e) {
        //todo create newnote.txt or newnote1.txt etc. and goto noteviewcontroller and load it in
    }

    @FXML
    public void deleteNote(ActionEvent e) {
        table.getSelectionModel().getSelectedIndex();

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
