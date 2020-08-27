package JavaFX;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class JavaFXTest extends Application {
    //todo change css to style wanted
    private PasswordField passwordText;

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
        passwordText = new PasswordField();
        Button login = new Button("login");
        login.setOnAction(e-> verifyPassword());
        login.getStyleClass().add("button-blue");
        login.setTooltip(new Tooltip("press to login"));

        Button switchAccount = new Button("switch/logout");

        HBox buttonHbox = new HBox(5);
        buttonHbox.getChildren().addAll(login, switchAccount);
        //ProgressBar pb = new ProgressBar();
        //pb.setPrefSize(200,30);

        GridPane.setConstraints(usernameLabel, 0,1);
        GridPane.setConstraints(passwordLabel, 0,3);
        GridPane.setConstraints(usernameText, 0, 2);
        GridPane.setConstraints(passwordText, 0, 4);
        //GridPane.setConstraints(login, 0, 5);
        GridPane.setConstraints(buttonHbox, 0, 5);
        //GridPane.setConstraints(pb, 1,4);

        ChoiceBox<String> switchChoice = new ChoiceBox<>();
        switchChoice.getItems().add("Switch");
        switchChoice.getItems().add("Logout");

        // todo put in switch account/logout button

        gridPane.getChildren().addAll(usernameLabel,  passwordLabel, usernameText, passwordText, buttonHbox);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPrefSize(500.0,500.0);


        Scene scene = new Scene(gridPane, 400,400);
        scene.getStylesheets().add("JavaFX/style.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);

        //how to set scene icon
        //todo change the shit logo Nathan made
        primaryStage.getIcons().add(new Image(
                        JavaFXTest.class.getResourceAsStream( "Logo.png")));

        primaryStage.show();
    }

    private void verifyPassword(){
        // todo use this method to validate the password
        System.out.println("kajhflakdjf");
    }
}
