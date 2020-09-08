import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class Controller {

    @FXML
    public void initialize() {
        loadCSSFiles();
        //other stuff you need on startup
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

    //copy anotation like this if you add a component using scene builder
    //you then need to make sure th fx:id is the same as below for you to use it in the code
    //see how I got the username and password for a better explenation

    //todo somehow it broke and I can't get the email and password anymore so I can't test this scene sliding yet
    @FXML public static TextField usernameField;
    @FXML public static PasswordField passField;

    @FXML
    ChoiceBox<String> switchCSS;
    ObservableList list = FXCollections.observableArrayList();

    private void loadCSSFiles() {
        list.removeAll();

        File[] files = new File("src\\userStyles").listFiles();
        ArrayList<String> cssFiles = new ArrayList<>();

        for (File f : files) {
            if (f.getName().endsWith(".css")) {
                cssFiles.add(f.getName().replace(".css", ""));
            }
        }

        list.addAll(cssFiles);
        switchCSS.getItems().addAll(list);
    }


    @FXML
    private void login(ActionEvent e) {
        System.out.println(usernameField.getText() + "," + passField.getText());
        loadCompose(e);
        //todo this will call loadEmail() to load the email frame you'll design: mallory
        //todo then when they press the compose button you have it'll load the email screen I made
    }

    @FXML
    private void loadCompose(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("compose.fxml"));
            Scene currentScene = usernameField.getScene();
            root.translateYProperty().set(currentScene.getHeight());
            parent.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.ONE, kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(evt -> {
                //todo remove old scene
            });
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
