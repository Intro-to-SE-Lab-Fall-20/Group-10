package JavaFX;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class JavaFXTest extends Application {
    private VBox mainCol;
    private PasswordField passwordText;
    private TextField usernameText;
    public Label welcomeLabel;
    public Label creatorNamesLabel;
    private User user = new User();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // todo change order of choice box and login button so that everything is defined that is needed before login
        primaryStage.setTitle("StraightShot");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(8);
        gridPane.setHgap(10);

        Label usernameLabel = new Label("Email: ");
        usernameLabel.getStyleClass().add("description-label");
        Label passwordLabel = new Label("Password: ");
        passwordLabel.getStyleClass().add("description-label");

        this.usernameText = new TextField();
        Tooltip loginFieldTip = new Tooltip("Complete email (ex: Straight@Shot.com)");
        loginFieldTip.getStyleClass().add("tooltip");
        this.usernameText.setTooltip(loginFieldTip);
        usernameText.getStyleClass().add("input-field");

        this.passwordText = new PasswordField();
        Tooltip passwordTip = new Tooltip("Password");
        passwordTip.getStyleClass().add("tooltip");
        passwordTip.getStyleClass().add("tooltip");
        this.passwordText.setTooltip(passwordTip);
        passwordText.getStyleClass().add("input-field");

        Button login = new Button("Login");
        login.setOnAction(e -> {
            encryptPassword(e);
            this.user.setUsername(this.usernameText.getText());
            this.user.setPassword(this.passwordText.getText());
            try {
                this.user.writeUser();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        login.getStyleClass().add("button-login");
        Tooltip loginTip = new Tooltip("Login");
        loginTip.getStyleClass().add("tooltip");
        login.setTooltip(loginTip);

        ChoiceBox switchCSS = new ChoiceBox();

        try {
            switchCSS.getItems().addAll(getCSSFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }

        switchCSS.setValue("style");
        switchCSS.getStyleClass().add("choice-box");
        Tooltip switchTooltip = new Tooltip("Switch between css styles");
        switchTooltip.getStyleClass().add("tooltip");
        switchCSS.setTooltip(switchTooltip);

        try {
            switchCSS.getSelectionModel().selectedItemProperty()
                    .addListener((ObservableValue observable, Object oldValue, Object newValue) -> user.setTheme(newValue.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        HBox buttonHbox = new HBox(5);
        buttonHbox.getChildren().addAll(login, switchCSS);

        Image image = new Image(JavaFXTest.class.getResourceAsStream("Logo.png"), 140, 140, false, true);

        GridPane.setConstraints(usernameLabel, 0, 1);
        GridPane.setConstraints(passwordLabel, 0, 3);
        GridPane.setConstraints(this.usernameText, 0, 2);
        GridPane.setConstraints(this.passwordText, 0, 4);
        GridPane.setConstraints(buttonHbox, 0, 5);

        gridPane.getChildren().addAll(usernameLabel, passwordLabel, this.usernameText, this.passwordText, buttonHbox);
        gridPane.setAlignment(Pos.CENTER);

        mainCol = new VBox();
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

        Scene scene = new Scene(mainCol, 400, 450);

        String styleFile = grabStyleFileName(user.getTheme());
        scene.getStylesheets().add(styleFile); // this changes the style to what the user last chose as their theme
        //todo add a way to utilize css file here depending on what user selected (will need data from user object)

        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.getIcons().add(new Image(
                JavaFXTest.class.getResourceAsStream("Logo.png")));
        primaryStage.show();
    }

    @Override
    public void stop() throws IOException {
        user.deleteUser();
    }

    private void encryptPassword(ActionEvent e) {
        String encryptedPassword = toHexString(getSHA(passwordText.getText().toCharArray()));
        System.out.println("Encrypted Password: " + encryptedPassword);
        test();
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

    //todo might not need this method but here incase we need a UUID generator
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

    //for mallory: https://www.youtube.com/watch?v=T3NlWMzPyXM&ab_channel=GenuineCoder
    //how to install the scene builder I use

    //todo to allow more robust solution to this problem can't you just reuse the method I did below
    //todo and then get the name from that?
    private String grabStyleFileName(String theme) {
        switch (theme) {
            case "blueStyle":
                return "JavaFX/styles/blueStyle.css";
            default:
                return "JavaFX/styles/style.css";
        }
    }

    private ArrayList getCSSFiles() throws Exception {
        File[] files = new File("src\\JavaFX\\styles").listFiles();
        ArrayList<String> cssFiles = new ArrayList<>();

        for (File f : files) {
            if (f.getName().endsWith(".css")) {
                cssFiles.add(f.getName().replace(".css", ""));
            }
        }

        if (cssFiles.isEmpty()) throw new Exception("No CSS file was found");

        return cssFiles;
    }

    private void openEmailGUI(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Make a cool transition here to the email screen");
    }

    private void test() {
        Properties props = new Properties();

        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "mail.dqnorthshore.com"); //todo this changes based on email domain
        props.put("mail.smtp.port", 587); //I believe 587 is std

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usernameText.getText(), passwordText.getText());
            }
        });

        Message message = prepareMessage(session, usernameText.getText(), "nathan.vincent.2.718@gmail.com");

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

    //todo make slide animation for mallory's email scene: nathan
    //todo make undecored scene with drag stuff: nathan
    //todo remove background gradient and allow customization of overall background in css:nathan
    //todo make bluecss: nathan
    private Scene getTestScene() {

        return null;
    }
}