import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

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
        //loader.getNamespace().put("theme", "@userStyles/pinkStyle.css");
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

        System.out.println("Compiled " + totalCodeLines(new File(System.getProperty("user.dir"))) + " lines of java code");
    }

    public int totalCodeLines(File startDir) {
        int ret = 0;

        if (startDir.isDirectory()) {
            File[] files = startDir.listFiles();

            for (File f : files)
                ret += totalCodeLines(f);
        } else if (startDir.getName().endsWith(".java") || startDir.getName().endsWith(".fxml")) {
            try {
                BufferedReader lineReader = new BufferedReader(new FileReader(startDir));
                String line = "";
                int localRet = 0;

                while ((line = lineReader.readLine()) != null)
                    localRet++;

                return localRet;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ret;
    }

    private boolean isFirstUser() {
        this.firstUser = true;
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("users.txt")) {
            JSONObject users = (JSONObject) parser.parse(reader);
            this.firstUser = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            this.firstUser = true;
            e.printStackTrace();
        }
        return firstUser;
    }
}
