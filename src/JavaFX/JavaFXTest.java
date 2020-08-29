package JavaFX;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class JavaFXTest extends Application {
    //todo change css to style wanted
    private PasswordField passwordText;
    private TextField usernameText;

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

        Label usernameLabel = new Label("Email: ");
        Label passwordLabel = new Label("Password: ");
        usernameText = new TextField();
        passwordText = new PasswordField();
        Button login = new Button("Login");
        login.setOnAction(e-> verifyPassword());
        login.getStyleClass().add("button-blue");
        login.setTooltip(new Tooltip("Login"));

        Button switchAccount = new Button("Switch Accounts");

        HBox buttonHbox = new HBox(5);
        buttonHbox.getChildren().addAll(login, switchAccount);

        GridPane gridpane = new GridPane();
        Image image = new Image(JavaFXTest.class.getResourceAsStream( "Logo.png"));

        //new ImageView(image)

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

        gridPane.getChildren().addAll(usernameLabel,  passwordLabel, usernameText, passwordText, buttonHbox);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPrefSize(500.0,500.0);

        HBox mainCol = new HBox();
        mainCol.setPadding(new Insets(10));
        mainCol.setSpacing(8);
        mainCol.getChildren().add(new Label("An email client designed by Mallory Duke and Nathan Cheshire"));

        //add scrolling text label
        //add picture
        //add gridPane
        //free cell
        //free cell

        Scene scene = new Scene(mainCol, 400,400);
        scene.getStylesheets().add("JavaFX/style.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);

        //how to set scene icon
        primaryStage.getIcons().add(new Image(
                        JavaFXTest.class.getResourceAsStream( "Logo.png")));

        primaryStage.show();
    }

    private void verifyPassword(){
        System.out.println(usernameText.getText() + "," + passwordText.getText());
    }
}
