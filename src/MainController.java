import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.UUID;

public class MainController {

    @FXML public AnchorPane parent;
    @FXML public TextField emailField;
    @FXML public PasswordField passField;
    @FXML public Button goBack;

    public static String emailAddress;
    public static char[] password ;

    public static Parent root;

    @FXML
    private void initialize() {
        try {
            File ddosFile = new File("users/" + MasterMainController.currentUser + "/d.dos");

            if (ddosFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(ddosFile));
                String[] parts = br.readLine().split(",");
                br.close();

                if (parts[0].equalsIgnoreCase("LOCKED")) {
                    emailField.setEditable(false);
                    passField.setEditable(false);
                    Main.startWorking("Lock out for 5 minutes",0);
                }

                long negTime = System.currentTimeMillis() - Long.parseLong(parts[1]);

                if (negTime > 0)
                    ddosFile.delete();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        ViewController.clearLocalAttachments();

        System.exit(0);
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

    //used for autologin for debugging
    private boolean silentValidateCredentials(String user, char[] pass) {
        StringBuilder passBuild = new StringBuilder();
        for (char c : pass) passBuild.append(c);

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect(getIMAPServer(user), user, passBuild.toString());
            return true;
        }

        catch (Exception ignored) {
            emailField.setText("");
            passField.setText("");
        }

        return false;
    }

    //see if we can access stuff from the email, if it's denied then it exists, if invalid then an error is thrown
    public boolean validateCredentials(String user, char[] pass) {
        StringBuilder passBuild = new StringBuilder();
        for (char c : pass) passBuild.append(c);

        try {
            Properties props = System.getProperties();

            if (user.endsWith("gmail.com")) {
                //we told the user to enable imaps and pop3 so this shouldn't be a problem
                props.setProperty("mail.store.protocol", "imaps");
                Session session = Session.getDefaultInstance(props, null);
                Store store = session.getStore("imaps");

                //if this doesn't work then we know the email couldn't be validated so an exception
                //will be thrown and we will informt the user we couldn't login
                store.connect(getIMAPServer(user), user, passBuild.toString());

                return true;
            }

            else if (user.endsWith("yahoo.com")) {
                //we told the user to enable imaps and pop3 so this shouldn't be a problem
                props.setProperty("mail.store.protocol", "imaps");
                Session session = Session.getDefaultInstance(props);
                Store store = session.getStore("imaps");

                //if this doesn't work then we know the email couldn't be validated so an exception
                //will be thrown and we will informt the user we couldn't login
                store.connect(getIMAPServer(user), user, passBuild.toString());

                return true;
            }

            else {
                //we told the user to enable imaps and pop3 so this shouldn't be a problem
                props.setProperty("mail.store.protocol", "imaps");
                Session session = Session.getDefaultInstance(props, null);
                Store store = session.getStore("imaps");

                //if this doesn't work then we know the email couldn't be validated so an exception
                //will be thrown and we will informt the user we couldn't login
                store.connect(getIMAPServer(user), user, passBuild.toString());

                return true;
            }
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();

            showPopupMessage("Sorry, " + System.getProperty("user.name") + ", but I couldn't validate\nthe email: " +
                    emailAddress, Main.primaryStage);

            ddosProtect(emailAddress);

            emailField.setText("");
            passField.setText("");
        }

        return false;
    }

    private void ddosProtect(String email) {
        try {
            File currentUserFolder = new File("users/" + MasterMainController.currentUser);
            File ddosFile = new File(currentUserFolder + "/d.dos");
            if (!ddosFile.exists()) {
                ddosFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(ddosFile,false));
                bw.write(System.currentTimeMillis() + "," + System.currentTimeMillis() + ",1");
                bw.flush();
                bw.close();
            }

            else {
                BufferedReader br = new BufferedReader(new FileReader(ddosFile));
                String[] parts = br.readLine().split(",");
                br.close();

                if (parts[0].equalsIgnoreCase("LOCKED")) {
                    emailField.setEditable(false);
                    emailField.setText("LOCKED OUT");
                    return;
                }

                long firstTime = Long.parseLong(parts[0]);
                long lastTime = Long.parseLong(parts[1]);
                int attempts = Integer.parseInt(parts[2]);

                attempts++;
                lastTime = System.currentTimeMillis();

                BufferedWriter bw = new BufferedWriter(new FileWriter(ddosFile,false));
                bw.write(firstTime + "," + lastTime + "," + attempts);
                bw.newLine();
                bw.flush();
                bw.close();

                long totalMilis = lastTime - firstTime;
                long minuteDif = (totalMilis / 1000 / 60);

                if (minuteDif <= 5 && attempts >= 10) {
                    bw = new BufferedWriter(new FileWriter(ddosFile,false));
                    bw.write("LOCKED," + (System.currentTimeMillis() + (300000)));
                    bw.newLine();
                    bw.flush();
                    bw.close();

                    emailField.setEditable(false);
                    passField.setEditable(false);
                    Main.startWorking("Lock out for 5 minutes",0);
                }

                else if (minuteDif >= 10) {
                    bw = new BufferedWriter(new FileWriter(ddosFile,false));
                    bw.write(lastTime + "," + lastTime + "," + 0);
                    bw.newLine();
                    bw.flush();
                    bw.close();
                }
            }
        }

        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void gotoNotes(ActionEvent e) {
        try {
            Main.startWorking("",1);
            Parent root = FXMLLoader.load(getClass().getResource("notemain.fxml"));
            // possible place to change style to user's chosen style
            Scene currentScene = emailField.getScene();
            root.translateXProperty().set(currentScene.getWidth());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent e) {
        try {
            Main.startWorking("",1);
            //load new parent and scene
            Parent root = FXMLLoader.load(getClass().getResource("MasterMain.fxml"));
            Scene currentScene = emailField.getScene();
            root.translateXProperty().set(-currentScene.getWidth()); //new scene starts off current scene

            //add the new scene
            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            //animate the scene in
            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);

            //play animation and when done, remove the old scene
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //main login method
    @FXML
    private void login(ActionEvent e) {
        emailAddress = emailField.getText();
        password = passField.getText().toCharArray();

        //make sure we can load stuff from the email and make sure it is formatted correctly using a regex
        if (isValidEmail(emailAddress) && validateCredentials(emailAddress, password)) {
            Main.startWorking("Email Authenticated",2500);
            loadEmail(e);
        }

        else {
            showPopupMessage("Sorry, " + System.getProperty("user.name") + ", but I couldn't validate\nthe email: " +
                    emailAddress, Main.primaryStage);

            ddosProtect(emailAddress);

            emailField.setText("");
            passField.setText("");
        }
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
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    //method explenation in EmailController
    @FXML
    private void loadEmail(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("email.fxml"));
            // possible place to change style to user's chosen style
            Scene currentScene = emailField.getScene();
            root.translateXProperty().set(currentScene.getWidth());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateXProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
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

    //open up a URL
    private void InternetConnect(String url) {
        try {
            //windows/OS X
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                //Ubuntu/Linux/Unix like distribution
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("/usr/bin/firefox -new-window " + url);
            }
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
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
