package JavaFX;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class JavaFXTest extends Application {
    private PasswordField passwordText;
    private TextField usernameText;
    public Label welcomeLabel;
    public Label creatorNamesLabel;

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
        Tooltip loginFieldTip = new Tooltip("Complete email (ex: Nathan@Domain.com)");
        loginFieldTip.getStyleClass().add("tooltip");
        usernameText.setTooltip(loginFieldTip);

        passwordText = new PasswordField();
        Tooltip passwordTip = new Tooltip("Password");
        passwordTip.getStyleClass().add("tooltip");
        passwordText.setTooltip(passwordTip);

        Button login = new Button("Login");
        login.setOnAction(e-> verifyPassword());
        login.getStyleClass().add("button-blue");
        Tooltip loginTip = new Tooltip("Login");
        loginTip.getStyleClass().add("tooltip");
        login.setTooltip(loginTip);

        Button switchAccount = new Button("Switch Accounts");
        switchAccount.getStyleClass().add("button-red");
        Tooltip switchTooltip = new Tooltip("Switch between other accounts");
        switchTooltip.getStyleClass().add("tooltip");
        switchAccount.setTooltip(switchTooltip);

        HBox buttonHbox = new HBox(5);
        buttonHbox.getChildren().addAll(login, switchAccount);

        GridPane gridpane = new GridPane();
        Image image = new Image(JavaFXTest.class.getResourceAsStream( "Logo.png"),140,140,false,true);

        GridPane.setConstraints(usernameLabel, 0,1);
        GridPane.setConstraints(passwordLabel, 0,3);
        GridPane.setConstraints(usernameText, 0, 2);
        GridPane.setConstraints(passwordText, 0, 4);
        GridPane.setConstraints(buttonHbox, 0, 5);

        gridPane.getChildren().addAll(usernameLabel,  passwordLabel, usernameText, passwordText, buttonHbox);
        gridPane.setAlignment(Pos.CENTER);

        VBox mainCol = new VBox();
        mainCol.setPadding(new Insets(10));
        mainCol.setSpacing(8);
        mainCol.setAlignment(Pos.CENTER);

        welcomeLabel = new Label("An email client designed");
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.getStyleClass().add("welcome-label");

        creatorNamesLabel = new Label("by Mallory Duke and Nathan Cheshire");
        creatorNamesLabel.setAlignment(Pos.CENTER);
        creatorNamesLabel.getStyleClass().add("welcome-label");

        ImageView logo = new ImageView(image);
        HBox imBox = new HBox();
        imBox.setAlignment(Pos.CENTER);
        imBox.getChildren().add(logo);

        Label cLabel = new Label("Dark Mode");
        cLabel.getStyleClass().add("dark-label");
        HBox darkBox = new HBox();
        darkBox.setPadding(new Insets(10));
        darkBox.setSpacing(8);
        darkBox.setAlignment(Pos.CENTER);

        CheckBox darkMode = new CheckBox();
        darkMode.setSelected(false);
        darkMode.getStyleClass().add("css-checkbox");
        Tooltip darkTooltip = new Tooltip("Enable Dark Mode");
        darkTooltip.getStyleClass().add("tooltip");
        darkMode.setTooltip(darkTooltip);

        darkBox.getChildren().addAll(darkMode,cLabel);

        mainCol.getChildren().addAll(welcomeLabel, creatorNamesLabel, imBox, gridPane,darkBox);

        Scene scene = new Scene(mainCol, 400,450);
        scene.getStylesheets().add("JavaFX/style.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);

        primaryStage.getIcons().add(new Image(
                        JavaFXTest.class.getResourceAsStream( "Logo.png")));

        primaryStage.show();

        //todo use all the info from the interface to store in a JSON file: mallory
    }

    private void verifyPassword(){
        System.out.println(usernameText.getText() + "," + passwordText.getText());
    }
}