import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ComposeController {

    private LinkedList<File> attachements;

    @FXML
    public Button attachButton;
    public TextField to;
    public TextField subject;
    public TextField carboncopy;
    public TextField blindcc;
    public TextArea emailContent;
    public static AnchorPane parent;

    @FXML
    public TableView table;
    public TableColumn name;
    public TableColumn size;
    public TableColumn type;

    @FXML
    public void initialize() {
        //init emailTable column names
        TableColumn nameCol = new TableColumn("Name");
        TableColumn sizeCol = new TableColumn("Size");
        TableColumn typeCol = new TableColumn("Type");

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //set how each column will display its data <AttachementPreview, String> means display this object as a string
        name.setCellValueFactory(new PropertyValueFactory<AttachementPreview, String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<AttachementPreview, String>("size"));
        type.setCellValueFactory(new PropertyValueFactory<AttachementPreview, String>("type"));

        table.getColumns().addListener((ListChangeListener) change -> {
            change.next();
            if(change.wasReplaced()) {
                table.getColumns().clear();
                table.getColumns().addAll(name,size,type);
            }
        });

        table.setRowFactory( tv -> {
            TableRow<AttachementPreview> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    try {
                        File openMeh = row.getItem().getPointerFile();
                        Desktop desktop = Desktop.getDesktop();

                        if(openMeh.exists())
                            desktop.open(openMeh);
                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return row ;
        });

        table.setOnKeyPressed(keyEvent -> {
            AttachementPreview selectedItem = (AttachementPreview) table.getSelectionModel().getSelectedItem();
            if ( selectedItem != null ) {
                if (keyEvent.getCode().equals(KeyCode.DELETE) || keyEvent.getCode().equals(KeyCode.BACK_SPACE)){
                    attachements.remove(0);
                    table.getItems().remove(table.getSelectionModel().getSelectedIndex());
                }
            }
        });
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("email.fxml"));
            Scene currentScene = attachButton.getScene();
            root.translateYProperty().set(-currentScene.getHeight());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0 , Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(parent));
            tim.play();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //makes sure we have at least one recipient
    private boolean hasARecipient(String recip) {
        String[] recips = recip.split(",");

        for (String email: recips) {
            if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
                    "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])" +
                    "|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-" +
                    "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
                return false;
        }

        return true;
    }

    //tells us if the requested ccs or bccs are valid
    private boolean validCarbonCopies(String cc) {
        String[] ccs = cc.split(",");

        for (String email: ccs) {
            if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
                    "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])" +
                    "|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-" +
                    "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
                return false;
        }

        return true;
    }

    //main driver method for StraightShot
    @FXML
    private void sendEmail(ActionEvent e) {
        try {
            //first get all the text from the fields
            String recipients = to.getText().trim();
            String carbonCopies = carboncopy.getText().trim();
            String blindCC = blindcc.getText().trim();
            String subjectText = subject.getText().trim();
            String content = emailContent.getText().trim();

            //now validate all the user input
            if (!hasARecipient(recipients) || recipients.trim().length() == 0) {
                showPopupMessage("Please check your recipients email addresses", Main.primaryStage);
            }

            else if (!validCarbonCopies(carbonCopies) && carbonCopies.trim().length() > 0) {
                showPopupMessage("Please check your cc emails", Main.primaryStage);
            }

            else if (!validCarbonCopies(blindCC) && blindCC.trim().length() > 0) {
                showPopupMessage("Please check your bcc emails", Main.primaryStage);
            }

            //it's valid, yay!
            else {
                //we can send an email with no subject or message body but we should inform the user to make sure it is intended
                if (subjectText.length() == 0) {
                    showPopupMessage("Please note there is no subject", Main.primaryStage);
                }

                if (content.length() == 0) {
                    showPopupMessage("Please note there is no message body", Main.primaryStage);
                }

                //init email and password
                String ourEmail = Controller.emailAddress;
                StringBuilder passwordBuilder = new StringBuilder();
                for (int i = 0 ; i < Controller.password.length ; i++)
                    passwordBuilder.append(Controller.password[i]);

                //init tls and smtp
                Properties props = new Properties();
                props.put("mail.smtp.auth", true);
                props.put("mail.smtp.starttls.enable", true);
                props.put("mail.smtp.host", getEmailHost(ourEmail));
                props.put("mail.smtp.port", 587);

                //create session
                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(ourEmail, passwordBuilder.toString()); }
                });

                //make a message
                Message mes = new MimeMessage(session);
                mes.setFrom(new InternetAddress(ourEmail));

                //add recipients, carbon copies, and blind carbon copies to our email
                InternetAddress[] addresses = InternetAddress.parse(recipients);
                mes.addRecipients(Message.RecipientType.TO, addresses);

                InternetAddress[] ccAddresses = InternetAddress.parse(carbonCopies);
                if (ccAddresses.length > 0) {
                    mes.addRecipients(Message.RecipientType.CC, ccAddresses);
                }

                InternetAddress[] bccAddresses = InternetAddress.parse(blindCC);
                if (bccAddresses.length > 0) {
                    mes.addRecipients(Message.RecipientType.BCC, bccAddresses);
                }

                //set subject, content, and body
                mes.setSubject(subjectText);

                Multipart emailContent = new MimeMultipart();

                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText(content);

                emailContent.addBodyPart(textBodyPart);

                //add attachements to our Multipart if we have any
                addAttachements(emailContent);

                mes.setContent(emailContent);

                //attempt to send and if successful, inform user and go back to inbox screen
                Transport.send(mes);

                showPopupMessage("Sent messaage", Main.primaryStage);

                goBack(null);
            }
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //must pass in a file that holds audio and this will return min:sec format of song length
    public static String getSongLength(File f) {
        try {
            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(f);
            Map properties = baseFileFormat.properties();
            Long duration = (Long) properties.get("duration");
            int seconds = (int) Math.round(duration / 1000.0 / 1000.0);
            int minutes = 0;

            while (seconds - 60 > 0) {
                minutes++;
                seconds -= 60;
            }

            return (seconds < 10 ? minutes + ":0" + seconds : minutes + ":" + seconds);
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //must pass in a file that is an iamge and will return [xDim, yDim] of the image
    private int[] getImageDimensions(File imageFile) {
        try {
            return new int[]{ImageIO.read(imageFile).getWidth(), ImageIO.read(imageFile).getHeight()};
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return new int[]{0,0};
    }

    //will return txt, png, mp3, or whatever the file type is
    private String getFileExtension(File f) {
        String extension = "";
        int i = f.getName().lastIndexOf('.');

        if (i > 0) {
            extension = f.getName().substring(i + 1);
        }

        return extension.replace(".","");
    }

    //returns a representation if a file in MB or KB with 2 decimal places
    private String getDisplayFileSize(File f) {
        DecimalFormat threeDecimal = new DecimalFormat("#.###");
        double mbSize = f.length() / 1024.0 / 1024.0;

        if (mbSize < 1)
            return threeDecimal.format((mbSize * 1024.0)) + " KB";
        else
            return threeDecimal.format(mbSize) + " MB";
    }

    //resolves email hosts if supported
    private String getEmailHost(String email) throws Exception {
        if (email.endsWith("gmail.com"))
            return "smtp.gmail.com";
        else if (email.endsWith("yahoo.com"))
            return "smtp.mail.yahoo.com";
        else if (email.endsWith("outlook.com"))
            return "smtp.office365.com";
        else
            throw new IllegalAccessException("Unsupported email host");
    }

    //this method adds attachements to the actual email that we are going to send
    private void addAttachements(Multipart multipart) {
        try {
            if (attachements != null && !attachements.isEmpty()) {
                for (File file : attachements) {
                    MimeBodyPart attachement = new MimeBodyPart();
                    attachement.attachFile(file);
                    multipart.addBodyPart(attachement);
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //used to add attachement representations to the table
    private void addAttachementsToTable(String name, String size, String type, File file) {
        table.getItems().add(new AttachementPreview(name,size,type, file));
        table.refresh();
    }

    @FXML
    private void addFiles(ActionEvent e) {
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Add Attachements");
            List<File> listAttachements = fc.showOpenMultipleDialog(null);
            attachements = new LinkedList<>();
            attachements.addAll(listAttachements);

            if (attachements != null) {
                for (File attachement : attachements) {
                    if (getFileExtension(attachement).equalsIgnoreCase("mp3")) {
                        addAttachementsToTable(attachement.getName().replace("." + getFileExtension(attachement),""),
                                getDisplayFileSize(attachement),getSongLength(attachement), attachement);
                    }

                    else if (getFileExtension(attachement).equalsIgnoreCase("png") ||
                            getFileExtension(attachement).equalsIgnoreCase("jpg") ||
                            getFileExtension(attachement).equalsIgnoreCase("jpeg")) {
                        int[] dim = getImageDimensions(attachement);
                        addAttachementsToTable(attachement.getName().replace("." + getFileExtension(attachement),""),
                                getDisplayFileSize(attachement),dim[0] + " x " + dim[1], attachement);
                    }

                    else {
                        addAttachementsToTable(attachement.getName().replace("." + getFileExtension(attachement),""),
                                getDisplayFileSize(attachement),getFileExtension(attachement), attachement);
                    }
                }
            }

            else
                System.out.println("Invalid file");
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
        FileWriter file;
        try {
            file = new FileWriter("user.txt");
            file.write("");
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }

    //popup messages, can customize look based on the style sheet selected
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
            popup.setY(stage.getY() + 25);
        });
        popup.show(stage);
    }
}