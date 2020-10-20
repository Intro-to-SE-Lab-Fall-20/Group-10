import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.Reader;

public class Main extends Application {
    // boolean value to determine if this is the first user or is a first time user
    private boolean firstUser;

    //stage to pass around to class that wish to add/remove stuff from it such as scenes or components
    static Stage primaryStage;

    //offsets used for window dragging
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("IO/Logo.png")));

        //allow the primary stage to be movable
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
            primaryStage.setOpacity(0.8f);
        });

        root.setOnDragDone((event -> Main.primaryStage.setOpacity(1.0f)));
        root.setOnMouseReleased((event -> Main.primaryStage.setOpacity(1.0f)));

        primaryStage.show();
    }

    private boolean isFirstUser() {
        this.firstUser = true;
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("users.txt")) {
            JSONObject users = (JSONObject) parser.parse(reader);
            this.firstUser = false;
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return firstUser;
    }
}
