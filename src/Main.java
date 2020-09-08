
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.UUID;

public class Main extends Application {

    public static Stage primaryStage;
    public User user = new User();

    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("IO/Logo.png")));

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
            primaryStage.setOpacity(0.8f);
        });

        root.setOnDragDone((event -> Main.primaryStage.setOpacity(1.0f)));
        root.setOnMouseReleased((event -> Main.primaryStage.setOpacity(1.0f)));

        primaryStage.show();
    }

    //todo make slide animation: nathan
    //todo make blueStyle a valid css file for this: nathan

    //for mallory: https://www.youtube.com/watch?v=T3NlWMzPyXM&ab_channel=GenuineCoder
    //how to install the scene builder I use

    //this is probably broken now but what did it do anyway?
    private String grabStyleFileName(String theme) {
        switch (theme) {
            case "blueStyle":
                return "sample/userStyles/blueStyle.css";
            default:
                return "sample/userStyles/style.css";
        }
    }

    private void openEmailGUI(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Make a cool transition here to the email screen");
    }

    private void test() {
        Properties props = new Properties();

        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "mail.dqnorthshore.com");
        props.put("mail.smtp.port", 587);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Controller.usernameField.getText(), Controller.passField.getText());
            }
        });

        Message message = prepareMessage(session, Controller.usernameField.getText(), "nathan.vincent.2.718@gmail.com");

        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws IOException {
        user.deleteUser();
    }

    private void encryptPassword(ActionEvent e) {
        String encryptedPassword = toHexString(getSHA(Controller.passField.getText().toCharArray()));
        System.out.println("Encrypted Password: " + encryptedPassword);
        test();
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
}
