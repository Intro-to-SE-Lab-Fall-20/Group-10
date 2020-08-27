package JavaFX;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;


public class JavaFXTest extends Application {
    //todo change css to style wanted
    private static String username;
    private String password;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("StraightShot");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        Label usernameLabel = new Label("username: ");
        Label passwordLabel = new Label("password: ");
        TextField usernameText = new TextField();
        PasswordField passwordText = new PasswordField();
        Button login = new Button("login");
        login.setOnAction(e-> verifyPassword(passwordText.getText()));
        login.getStyleClass().add("button-blue");
        ProgressBar pb = new ProgressBar();
        pb.setPrefSize(200,30);

        GridPane.setConstraints(usernameLabel, 0,0);
        GridPane.setConstraints(passwordLabel, 0,1);
        GridPane.setConstraints(usernameText, 1, 0);
        GridPane.setConstraints(passwordText, 1, 1);
        GridPane.setConstraints(login, 1, 2);
        GridPane.setConstraints(pb, 1,4);

        gridPane.getChildren().addAll(usernameLabel,  passwordLabel, usernameText, passwordText, login, pb);

        Scene scene = new Scene(gridPane, 400,400);
        scene.getStylesheets().add("JavaFX/style.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        //how to set scene icon
        //todo change the shit logo Nathan made
        primaryStage.getIcons().add(new Image(
                        JavaFXTest.class.getResourceAsStream( "StraightIcon.png")));

        primaryStage.show();
    }

    private void verifyPassword(String text){
        // todo use this method to validate the password
        System.out.println("kajhflakdjf");
    }
}
