import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class Controller {

    @FXML
    public void initialize() {
        loadCSSFiles();
        //other stuff you need on startup
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        //here you can save user stuff like theme and what not: mallory
        System.exit(0);
    }

    //copy anotation like this if you add a component using scene builder
    //you then need to make sure th fx:id is the same as below for you to use it in the code
    //see how I got the username and password for a better explenation

    @FXML public AnchorPane parent;
    @FXML public TextField emailField;
    @FXML public PasswordField passField;

    @FXML
    ChoiceBox<String> switchCSS;
    ObservableList list = FXCollections.observableArrayList();

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
    }

    @FXML
    private void login(ActionEvent e) {
        System.out.println(emailField.getText() + "," + passField.getText());
        loadCompose(e);
        //todo this will call loadEmail() to load the email frame you'll design: mallory
        //todo then when they press the compose button you have it'll load the email screen I made
    }

    private void encryptPassword(ActionEvent e) {
        String encryptedPassword = toHexString(getSHA(passField.getText().toCharArray()));
        System.out.println("Encrypted Password: " + encryptedPassword);
    }

    private void testSendEmail() {
        Properties props = new Properties();

        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "mail.dqnorthshore.com");
        props.put("mail.smtp.port", 587);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailField.getText(), passField.getText());
            }
        });

        Message message = prepareMessage(session, emailField.getText(), "nathan.vincent.2.718@gmail.com");

        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Message prepareMessage(Session session, String account, String recip) {
        try {
            Message mes = new MimeMessage(session);
            mes.setFrom(new InternetAddress(account));
            mes.setRecipient(Message.RecipientType.TO, new InternetAddress(recip));
            mes.setSubject("Subject about java mail for straightshot");
            mes.setText("Hide the body as soon as you can!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Secure Hashing Algorithm 256 bit std encryption
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

    //returns hex representation of bit string
    public String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    //not need this method but here incase we need a UUID generator
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

    @FXML
    private void loadCompose(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("compose.fxml"));
            Scene currentScene = emailField.getScene();
            root.translateYProperty().set(currentScene.getHeight());
            //getWidth, translateXProperty in both places for sliding to the main email scene that mallory will design
            parent.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(evt -> {
                //todo remove old scene
            });
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
