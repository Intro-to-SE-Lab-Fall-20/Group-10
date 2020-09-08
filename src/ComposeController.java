import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class ComposeController {

    @FXML
    public void initialize() {
        //stuff you need on startup of compose page
    }

    @FXML
    public static AnchorPane parent;

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        //here you can save user stuff like theme and what not: mallory
        System.exit(0);
    }
}