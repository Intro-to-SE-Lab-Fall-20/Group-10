import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ComposeController {

    private List<File> attachements;

    @FXML
    public Button attachButton;

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
            FileChooser fc = new FileChooser();
            fc.setTitle("Add Attachements");
            attachements = fc.showOpenMultipleDialog(null);

            if (attachements != null) {
                StringBuilder build = new StringBuilder();

                for (File attachement : attachements) {
                    build.append(attachement.getName()).append(" ").append((int) Math.ceil(attachement.length() / 1024.0)).append("KB").append("\n");
                }

                //todo change this to a list view where the user can delete individual files
                //attachements list will stay because that's what we'll use when sending emails
                attachButton.setTooltip(new Tooltip(build.toString()));
            }

            else {
                System.out.println("Invalid file");
            }
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
        FileWriter file;
        try {
            file = new FileWriter("user.txt");
            file.write("");
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }
}