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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFXTest extends Application {
    //todo change css to style wanted
    private PasswordField passwordText;
    private TextField usernameText;
    public Label welcomeLabel;
    private String welcomeString = "An email client designed\nby Mallory Duke and Nathan Cheshire";

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
        //todo change buttons to nicer looking with tooltips and popups

        HBox buttonHbox = new HBox(5);
        buttonHbox.getChildren().addAll(login, switchAccount);

        GridPane gridpane = new GridPane();
        Image image = new Image(JavaFXTest.class.getResourceAsStream( "Logo.png"),100,100,true,true);

        GridPane.setConstraints(usernameLabel, 0,1);
        GridPane.setConstraints(passwordLabel, 0,3);
        GridPane.setConstraints(usernameText, 0, 2);
        GridPane.setConstraints(passwordText, 0, 4);
        GridPane.setConstraints(buttonHbox, 0, 5);

        ChoiceBox<String> switchChoice = new ChoiceBox<>();
        switchChoice.getItems().add("Switch");
        switchChoice.getItems().add("Logout");

        gridPane.getChildren().addAll(usernameLabel,  passwordLabel, usernameText, passwordText, buttonHbox);
        gridPane.setAlignment(Pos.CENTER);
        //gridPane.setPrefSize(500.0,500.0);

        VBox mainCol = new VBox();
        mainCol.setPadding(new Insets(10));
        mainCol.setSpacing(8);
        mainCol.setAlignment(Pos.CENTER);

        welcomeLabel = new Label(welcomeString);
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.getStyleClass().add("welcome-label");

//        Platform.runLater(() -> {
//            while (true) {
//                Platform.runLater(() -> {
//                    try {
//                        welcomeLabel.setText("hi");
//                        System.out.println("test");
//                        Thread.sleep(1000);
//                    }
//
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//            }
//        });
        //todo trying to do a scrolling label but it's being a bitch :(

        ImageView logo = new ImageView(image);
        HBox imBox = new HBox();
        imBox.setAlignment(Pos.CENTER);
        imBox.getChildren().add(logo);
        //todo image border

        mainCol.getChildren().addAll(welcomeLabel, imBox, gridPane, new VBox(), new VBox());

        Scene scene = new Scene(mainCol, 400,400);
        scene.getStylesheets().add("JavaFX/style.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);

        //how to set scene icon
        primaryStage.getIcons().add(new Image(
                        JavaFXTest.class.getResourceAsStream( "Logo.png")));

        primaryStage.show();

        //todo create checkboxes that switch between different css files to give the user a theme option
        //todo use all the info from the interface to store in a JSON file
    }

    private void verifyPassword(){
        System.out.println(usernameText.getText() + "," + passwordText.getText());
    }
}
