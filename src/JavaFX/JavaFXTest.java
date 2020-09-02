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

import java.io.IOException;

public class JavaFXTest extends Application {
    private PasswordField passwordText;
    private TextField usernameText;
    public Label welcomeLabel;
    public Label creatorNamesLabel;
    private User user = new User();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {
        primaryStage.setTitle("StraightShot");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        Label usernameLabel = new Label("Email: ");
        Label passwordLabel = new Label("Password: ");

        this.usernameText = new TextField();
        Tooltip loginFieldTip = new Tooltip("Complete email (ex: Nathan@Domain.com)");
        loginFieldTip.getStyleClass().add("tooltip");
        this.usernameText.setTooltip(loginFieldTip);

        this.passwordText = new PasswordField();
        Tooltip passwordTip = new Tooltip("Password");
        passwordTip.getStyleClass().add("tooltip");
        this.passwordText.setTooltip(passwordTip);

        Button login = new Button("Login");
        login.setOnAction(e-> {
            verifyPassword();
            this.user.setUsername(this.usernameText.getText());
            this.user.setPassword(this.passwordText.getText());
            try {
                this.user.writeUser();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        login.getStyleClass().add("button-blue");
        Tooltip loginTip = new Tooltip("Login");
        loginTip.getStyleClass().add("tooltip");
        login.setTooltip(loginTip);

        Button switchCSS = new Button("Switch style");
        switchCSS.getStyleClass().add("button-red");
        Tooltip switchTooltip = new Tooltip("Switch between css styles");
        switchTooltip.getStyleClass().add("tooltip");
        switchCSS.setTooltip(switchTooltip);

        HBox buttonHbox = new HBox(5);
        buttonHbox.getChildren().addAll(login, switchCSS);

        GridPane gridpane = new GridPane();
        Image image = new Image(JavaFXTest.class.getResourceAsStream( "Logo.png"),140,140,false,true);

        GridPane.setConstraints(usernameLabel, 0,1);
        GridPane.setConstraints(passwordLabel, 0,3);
        GridPane.setConstraints(this.usernameText, 0, 2);
        GridPane.setConstraints(this.passwordText, 0, 4);
        GridPane.setConstraints(buttonHbox, 0, 5);

        gridPane.getChildren().addAll(usernameLabel,  passwordLabel, this.usernameText, this.passwordText, buttonHbox);
        gridPane.setAlignment(Pos.CENTER);

        VBox mainCol = new VBox();
        mainCol.setPadding(new Insets(10));
        mainCol.setSpacing(8);
        mainCol.setAlignment(Pos.CENTER);

        this.welcomeLabel = new Label("An email client designed");
        this.welcomeLabel.setAlignment(Pos.CENTER);
        this.welcomeLabel.getStyleClass().add("welcome-label");

        this.creatorNamesLabel = new Label("by Mallory Duke and Nathan Cheshire");
        this.creatorNamesLabel.setAlignment(Pos.CENTER);
        this.creatorNamesLabel.getStyleClass().add("welcome-label");

        ImageView logo = new ImageView(image);
        HBox imBox = new HBox();
        imBox.setAlignment(Pos.CENTER);
        imBox.getChildren().add(logo);

        mainCol.getChildren().addAll(this.welcomeLabel, this.creatorNamesLabel, imBox, gridPane);

        Scene scene = new Scene(mainCol, 400,450);
        scene.getStylesheets().add("JavaFX/styles/style.css");

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.getIcons().add(new Image(
                        JavaFXTest.class.getResourceAsStream( "Logo.png")));
        primaryStage.show();
    }

    @Override
    public void stop() throws IOException {
        user.deleteUser();
        // confusion
    }

    private void verifyPassword(){
        System.out.println(this.usernameText.getText() + "," + this.passwordText.getText());
    }
}