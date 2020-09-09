import javafx.event.ActionEvent;
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
    private void goBack(ActionEvent event) {
        try {
            System.out.println("Scroll email pane back down");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendEmail(ActionEvent e) {
        try {
            System.out.println("Grab information from fields and form email based on it and send.\n" +
                    "Inform user of successful send");
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void addFiles(ActionEvent e) {
        try {
            System.out.println("FileChooser to allow any files, display as a list where you can see" +
                    "\nindividual file sizes and remove/replace each attachement");
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
        //here you can save user stuff like theme and what not: mallory
        System.exit(0);
    }
}