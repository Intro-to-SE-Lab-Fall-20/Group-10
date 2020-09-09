import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;

public class EmailController {

    @FXML
    public void initialize() {
        //stuff you need on startup of compose page
    }

    @FXML
    public static AnchorPane parent;
    @FXML
    public Button composeButton;
    @FXML
    public Button logoutButton;

    @FXML
    private void gotoCompose(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("compose.fxml"));
            Scene currentScene = logoutButton.getScene();
            root.translateYProperty().set(currentScene.getHeight());

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

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene currentScene = logoutButton.getScene();
            root.translateXProperty().set(-currentScene.getWidth());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
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