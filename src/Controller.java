import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.mail.Session;
import javax.mail.Store;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class Controller {
    //do we need this at all? (static since only one user can be logged in at any one time)
    // yes bc it is used in login method
    public static User user;

    @FXML public StackPane masterStack;
    @FXML public AnchorPane parent;
    @FXML public TextField emailField;
    @FXML public PasswordField passField;
    @FXML
    ChoiceBox<String> switchCSS;
    ObservableList list = FXCollections.observableArrayList();

    public static String emailAddress;
    public static char[] password;
    public static String theme;

    @FXML
    public void initialize() {
        loadCSSFiles();
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        FileWriter file;
        try {
            file = new FileWriter("user.txt");
            file.write("");
            file.close();
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    //finds css files and allows user to select any one
    //todo be able to set this to the style used for EmailController and ComposeController
    private void loadCSSFiles() {
        list.removeAll();

        File[] files = new File("src\\userStyles").listFiles();
        ArrayList<String> cssFiles = new ArrayList<>();

        for (File f : files) {
            if (f.getName().endsWith(".css")) {
                cssFiles.add(f.getName().replace(".css", ""));
            }
        }

        list.addAll(cssFiles);
        switchCSS.getItems().addAll(list);
        switchCSS.getSelectionModel().select(0);
        switchCSS.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> theme = String.valueOf(new_val));
        if(theme == null) theme = "default";
    }


    //return false if it is not formatted like an email should be
    //examples: mnd199@msu.edu, nvc29@msstate.edu, Bryan.jones@cpe.msstate.edu, nathan@gmail.com
    private boolean isValidEmail(String email) {

        //Don't ask me how it works; I'll get a headache again
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
                "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])" +
                "|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-" +
                "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
            return false;


        return (email.endsWith("@gmail.com") ||
                email.endsWith("@yahoo.com") ||
                email.endsWith("@outlook.com")) &&
                email.trim().length() > 0;
    }

    //gets the iamp server based on the email provdider
    public String getIMAPServer(String email) {
        if (email.endsWith("gmail.com"))
            return "imap.gmail.com";
        else if (email.endsWith("yahoo.com"))
            return "imap.mail.yahoo.com";
        else if (email.endsWith("outlook.com"))
            return "imap-mail.outlook.com";
        else
            return null;
    }

    //see if we can access stuff from the email, if it's denied then it exists, if invalid then an error is thrown
    public boolean validateCredentials(String user, char[] pass) {
        StringBuilder passBuild = new StringBuilder();
        for (char c : pass) passBuild.append(c);

        Properties props = System.getProperties();

        //we told the user to enable imaps and pop3 so this shouldn't be a problem
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");

            //if this doesn't work then we know the email couldn't be validated so an exception
            //will be thrown and we will informt the user we couldn't login
            store.connect(getIMAPServer(user), user, passBuild.toString());

            return true;
        }

        catch (Exception e) {
            System.out.println("Account DNE");
        }

        return false;
    }

    //main login method
    @FXML
    private void login(ActionEvent e) {
        emailAddress = emailField.getText();
        password = passField.getText().toCharArray();

        //make sure we can load stuff from the email and make sure it is formatted correctly using a regex
        if (isValidEmail(emailAddress) && validateCredentials(emailAddress, password)) {
            this.user = new User(emailField.getText(), theme);

            try {
                this.user.writeUser();
            }

            catch (IOException ex) {
                ex.printStackTrace();
            }

            loadCompose(e);
        }

        else {
            showPopupMessage("Sorry, " + System.getProperty("user.name") + ", but I couldn't validate\nthe email: " +
                    emailAddress, Main.primaryStage);

            emailField.setText("");
            passField.setText("");
        }
    }

    //get the email host, will remove cases if we cannot support any other mail services aside from (gmail)
    private String getEmailHost(String email) throws Exception {
        if (email.endsWith("gmail.com"))
            return "smtp.gmail.com";
        else if (email.endsWith("yahoo.com"))
            return "smtp.mail.yahoo.com";
        else if (email.endsWith("outlook.com"))
            return "smtp.office365.com";
        else
            throw new Exception("Unsupported email host");
    }

    //Secure Hashing Algorithm 256-bit (standard encryption for modern browsers)
    public byte[] getSHA(char[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(new String(input).getBytes(StandardCharsets.UTF_8));
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    //returns hex representation of bin string
    public String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    //UUID generator
    public String generateUUID() {
        try {
            MessageDigest salt =MessageDigest.getInstance("SHA-256");
            salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
            return UUID.nameUUIDFromBytes(salt.digest()).toString();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //method explenation in EmailController
    @FXML
    private void loadCompose(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("email.fxml"));
            Scene currentScene = emailField.getScene();
            root.translateXProperty().set(currentScene.getWidth());
            masterStack.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> masterStack.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //used by below method (may custom in CSS file if you wish)
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

    //todo make these look nicer, maybe slide in and out like in cyder? And also make timeout after 5 sec
    //show a popup message
    private void showPopupMessage(final String message, final Stage stage) {
        final Popup popup = createPopup(message);
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + stage.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(stage.getY() + 25);
        });
        popup.show(stage);
    }

    //open up a URL
    private void InternetConnect(String url) {
        try {
            //windows/OS X
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                //Ubuntu/Linux dist
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("/usr/bin/firefox -new-window " + url);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    //this opens google queries that will help the user setup their email for SS use
    @FXML
    private void enableIMAP() {
        InternetConnect("https://shorturl.at/dgprT");
        InternetConnect("https://shorturl.at/gsHO7");
    }
}
