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

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class RegisterController {

    @FXML public TextField newusernameField;
    @FXML public PasswordField passField;
    @FXML public PasswordField confPassword;
    @FXML public Button registerLogin;
    @FXML public Button cancel;
    @FXML public Label passMatch;

    public Parent root;

    @FXML
    private void initialize() {
        confPassword.setOnKeyReleased(event -> {
            if (passField.getText().equals(confPassword.getText())) {
                if (passField.getText().length() >= 5) {
                    passMatch.setStyle("-fx-text-fill:  rgb(60, 167, 92)");
                    passMatch.setText("Passwords match, good job");
                }

                else {
                    passMatch.setText("Password should be > 4 characters");
                }
            }

            else {
                passMatch.setStyle("-fx-text-fill:  rgb(223,85,83)");
                passMatch.setText("Passwords do not match");
            }
        });

        passField.setOnKeyReleased(event -> {
            if (passField.getText().equals(confPassword.getText())) {
                if (passField.getText().length() >= 5) {
                    passMatch.setStyle("-fx-text-fill:  rgb(60, 167, 92)");
                    passMatch.setText("Passwords match, good job");
                }

                else {
                    passMatch.setText("Password should be > 4 characters");
                }
            }

            else {
                passMatch.setStyle("-fx-text-fill:  rgb(223,85,83)");
                passMatch.setText("Passwords do not match");
            }
        });
    }

    @FXML
    public void login(ActionEvent e) {
        try {
            File usersFile = new File("users.csv");
            if (!usersFile.exists()) usersFile.createNewFile();

            String newUserName = newusernameField.getText();
            char[] password = passField.getText().toCharArray();
            char[] passwordConf = confPassword.getText().toCharArray();

            boolean valid = true;
            boolean alreadyExist = false;

            if (password.length != passwordConf.length)
                valid = false;

            for (int c = 0 ; c < password.length ; c++)
                if (password[c] != passwordConf[c])
                    valid = false;

            if (password.length < 5)
                valid = false;

            if (!check(newUserName))
                valid = false;

            if (alreadyExists(newUserName)) {
                valid = false;
                alreadyExist = true;
            }

            if (valid) {
                String wl = newUserName.toLowerCase() + "," + toHexString(getSHA(password));

                BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile, true));
                bw.write(wl);
                bw.newLine();
                bw.flush();
                bw.close();

                loadEmailMain();

                MasterMainController.currentUser = newUserName;

                showPopupMessage("Successfully registered user " + newUserName,Main.primaryStage);
            }

            else {
                if (alreadyExist) {
                    showPopupMessage("Sorry, but that username is already in use. Please choose a different one", Main.primaryStage);
                    newusernameField.setText("");
                    passField.setText("");
                    confPassword.setText("");
                } else {
                    showPopupMessage("Sorry, but I could not register a user based on these" +
                            " credentials. Please make sure your username is alpha-numeric and your password" +
                            " is greater than 4 characters", Main.primaryStage);
                    newusernameField.setText("");
                    passField.setText("");
                    confPassword.setText("");
                }
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadEmailMain() {
        try {
            root = FXMLLoader.load(EmailController.class.getResource("main.fxml"));
            Scene currentScene = passField.getScene();
            root.translateXProperty().set(currentScene.getWidth());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);

            tim.getKeyFrames().add(kf);
            tim.play();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goBack(ActionEvent e) {
        newusernameField.setText("");
        passField.setText("");
        confPassword.setText("");

        Scene currentScene = registerLogin.getScene();
        StackPane pc = (StackPane) currentScene.getRoot();

        MasterMainController.root.translateYProperty().set(0);
        Timeline tim1 = new Timeline();
        KeyValue kv1 = new KeyValue(MasterMainController.root.translateYProperty(), currentScene.getHeight(), Interpolator.EASE_IN);
        KeyFrame kf1 = new KeyFrame(Duration.seconds(0.5), kv1);
        tim1.getKeyFrames().add(kf1);
        tim1.setOnFinished(ex -> pc.getChildren().remove( MasterMainController.root));
        tim1.play();
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

    boolean check(String username) {
        if (username == null)
            return false;

        int len = username.length();

        for (int i = 0; i < len; i++)
            if (!Character.isLetterOrDigit(username.charAt(i)))
                return false;

        return true;
    }

    private boolean alreadyExists(String check) {
        boolean ret = false;

        try {
            BufferedReader bw = new BufferedReader(new FileReader(new File("users.csv")));

            String line = bw.readLine();

            while (line != null) {
                String part = line.split(",")[0];

                if (part.equalsIgnoreCase(check))
                    ret = true;

                line = bw.readLine();
            }

            bw.close();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return ret;
    }
}
