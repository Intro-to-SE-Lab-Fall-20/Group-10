import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MasterMainController {

    @FXML public StackPane masterStack;
    @FXML public Button login;
    @FXML public Button loginButton;
    @FXML public TextField usernameField;
    @FXML public PasswordField passwordField;
    @FXML public Button registerButton;

    public static Parent root;

    @FXML
    public void register(ActionEvent e) {
        usernameField.setText("");
        passwordField.setText("");

        try {
            root = FXMLLoader.load(getClass().getResource("register.fxml"));
            Scene currentScene = registerButton.getScene();
            root.translateYProperty().set(currentScene.getHeight());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);

            tim.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void login(ActionEvent e) {
        String username = usernameField.getText();
        char[] password = passwordField.getText().toCharArray();
        String sha = toHexString(getSHA(password));
        String combo = username.toLowerCase() + "," + sha;

        boolean validUser = false;

        try {
            File usersFile = new File("users.csv");
            if (!usersFile.exists()) usersFile.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(usersFile));

            String line = br.readLine();

            while (line != null) {
                if (line.equals(combo))
                    validUser = true;

                line = br.readLine();
            }

            if (validUser) {
                usernameField.setText("");
                passwordField.setText("");

                try {
                    root = FXMLLoader.load(EmailController.class.getResource("main.fxml"));
                    Scene currentScene = passwordField.getScene();
                    root.translateXProperty().set(currentScene.getWidth());

                    StackPane pc = (StackPane) currentScene.getRoot();
                    pc.getChildren().add(root);

                    Timeline tim = new Timeline();
                    KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
                    KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);

                    tim.getKeyFrames().add(kf);
                    tim.play();
                }

                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            else {
                showPopupMessage("Sorry, but I could not validate the user " + username, Main.primaryStage);
                usernameField.setText("");
                passwordField.setText("");
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        System.exit(0);
    }

    //fx popup as opposed to the swing popup inside of main
    private Popup createPopup(final String message) {
        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        Label label = new Label(message);
        label.setOnMouseReleased(e -> popup.hide());
        label.getStylesheets().add("DefaultStyle.css");
        label.getStyleClass().add("popup");
        popup.getContent().add(label);
        return popup;
    }

    private void showPopupMessage(final String message, final Stage stage) {
        final Popup popup = createPopup(message);
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + stage.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(stage.getY() + 20);
        });
        popup.show(stage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    public byte[] getSHA(char[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();

            for (char c : input)
                sb.append(c);

            return md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}
