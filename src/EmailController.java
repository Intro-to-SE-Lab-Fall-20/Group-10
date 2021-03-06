import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.*;
import java.util.function.Function;

public class EmailController {
    //all gui elements
    @FXML
    public Label inboxLabel;
    public TableView<EmailPreview> table;
    public TableColumn message;
    public TableColumn from;
    public TableColumn date;
    public TableColumn subject;
    public static AnchorPane parent;
    public Button composeButton;
    public Button logoutButton;
    public Button deleteButton;
    public Button forwardButton;
    public Button replyButton;
    public Label unreadEmailsLabel;
    public CheckBox hideOnCloseCheckBox;
    public ChoiceBox<String> folderChoiceBox;
    public TextField searchFolderField;
    public Button refreshButton;

    //used so that we don't have to load emailcontroller everytime we come back here
    public static Parent root;
    public static Parent viewRoot;

    //current email folder
    private Message[] messages;
    public String currentFolder = "0";

    //viewing message content
    public static Multipart currentMessageMultipart;
    public static Message currentMessage;
    public static String currentMessageFrom;
    public static String currentMessageSubject;
    public static String currentMessageBody;
    public static LinkedList<File> currentMessageAttachments;
    public static String currentMessageDate;

    public ObservableList folderList = FXCollections.observableArrayList();

    public static String currentFolderName;

    private Store store;
    private Folder emailFolder;

    @FXML
    public void initialize() {
        try {
            //update the email address label

            inboxLabel.setText("Viewing inbox of: " + getEmailAddress());

            //set how each column will display its data <EmailPreview, String> means display this object as a string
            from.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("from"));
            date.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("date"));
            subject.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("subject"));
            message.setCellValueFactory(new PropertyValueFactory<EmailPreview, String>("message"));

            table.setColumnResizePolicy((param) -> false);

            //don't let the user rearrange the column ordering
            table.getColumns().addListener((ListChangeListener) change -> {
                change.next();
                if(change.wasReplaced()) {
                    table.getColumns().clear();
                    table.getColumns().addAll(from,date,subject,message);
                }
            });

            //these methods are for tooltips when you hover over an email in the list
            from.setCellFactory(new Callback<TableColumn<EmailPreview,String>, TableCell<EmailPreview,String>>() {
                @Override
                public TableCell<EmailPreview, String> call( TableColumn<EmailPreview, String> param) {
                    return new TableCell<>() {
                        private Text text;

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                text = new Text(item);
                                text.setWrappingWidth(80);
                                setGraphic(text);
                            }
                        }
                    };
                }
            });

            date.setCellFactory(new Callback<TableColumn<EmailPreview,String>, TableCell<EmailPreview,String>>() {
                @Override
                public TableCell<EmailPreview, String> call( TableColumn<EmailPreview, String> param) {
                    return new TableCell<>() {
                        private Text text;

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                text = new Text(item);
                                text.setWrappingWidth(100);
                                setGraphic(text);
                            }
                        }
                    };
                }
            });

            subject.setCellFactory(new Callback<TableColumn<EmailPreview,String>, TableCell<EmailPreview,String>>() {
                @Override
                public TableCell<EmailPreview, String> call( TableColumn<EmailPreview, String> param) {
                    return new TableCell<>() {
                        private Text text;

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                text = new Text(item);
                                text.setWrappingWidth(80);
                                setGraphic(text);
                            }
                        }
                    };
                }
            });

            message.setCellFactory(new Callback<TableColumn<EmailPreview,String>, TableCell<EmailPreview,String>>() {
                @Override
                public TableCell<EmailPreview, String> call( TableColumn<EmailPreview, String> param) {
                    return new TableCell<>() {
                        private Text text;

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                text = new Text(item);
                                text.setWrappingWidth(100);
                                setGraphic(text);
                            }
                        }
                    };
                }
            });

            //open the email on mouse click if we can
            table.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    try {
                        int index = messages.length - table.getSelectionModel().getSelectedIndex() - 1;

                        if (index >= 0 && index < messages.length) {
                            currentMessage = messages[index];

                            if (getMessageText(currentMessage).length() > 0)
                                currentMessageMultipart = (MimeMultipart) currentMessage.getContent();
                        }
                    }

                    catch (ClassCastException cce) {
                        cce.printStackTrace();
                        Main.startWorking("Message has no text",1000);
                    }

                    catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                if (event.getClickCount() > 1 && (messages.length - table.getSelectionModel().getSelectedIndex() - 1 < messages.length) && getMessageText(messages[messages.length - table.getSelectionModel().getSelectedIndex() - 1]).length() > 0) {
                    try {
                        gotoViewer(messages[messages.length - table.getSelectionModel().getSelectedIndex() - 1]);

                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            //init user folders like inbox
            initFolders();

            folderChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, n1, n2) -> {
                String folder = folderChoiceBox.getItems().get((Integer) ov.getValue());
                fetchEmail(folder);
            });

            String[] choices = folderChoiceBox.getItems().toArray(new String[0]);
            int matchIndex = 0;

            for (int i = 0 ; i < choices.length ; i++)
                if (choices[i].equalsIgnoreCase("Inbox"))
                    matchIndex = i;

            folderChoiceBox.getSelectionModel().select(matchIndex);

            //prepare the textfield used for searching through emails
            searchFolderField.setOnAction(event -> {
                try {
                    fetchEmail(currentFolder);
                    String searchFor = searchFolderField.getText();

                    if (searchFor.isEmpty()) {
                        fetchEmail(currentFolder);
                        return;
                    }

                    List<EmailPreview> previewList = table.getItems();
                    List<EmailPreview> removes = new LinkedList<>();

                    for (EmailPreview EM : previewList) {
                        if (!EM.getFullSubject().toLowerCase().contains(searchFor.toLowerCase())
                        && !EM.getFullMessage().toLowerCase().contains(searchFor.toLowerCase())
                        && !EM.getFullFrom().toLowerCase().contains(searchFor.toLowerCase())
                        && !EM.getFullDate().toLowerCase().contains(searchFor.toLowerCase()))
                            removes.add(EM);
                    }

                    for (EmailPreview ep : removes)
                        table.getItems().remove(ep);
                }

                catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            //deleting emails function
            table.setOnKeyPressed(keyEvent -> {
                EmailPreview selectedItem = table.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    if (keyEvent.getCode().equals(KeyCode.DELETE) || keyEvent.getCode().equals(KeyCode.BACK_SPACE)){
                        deleteEmail();
                        table.refresh();
                    }
                }
            });

            fetchEmail("Inbox");
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
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

    //simple email getter
    private String getEmailAddress() {
        return MainController.emailAddress;
    }

    //initialize folders choicebox for email address
    private void initFolders() {
        try {
            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < MainController.password.length; i++)
                passwordBuilder.append(MainController.password[i]);

            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", (getEmailAddress()));
            props.put("mail.smtp.port", 587);
            props.put("mail.imap.fetchsize", "4096");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(getEmailAddress(), passwordBuilder.toString());
                        }
                    });

            Store store = session.getStore("imaps");
            store.connect(getIMAPServer(getEmailAddress()), getEmailAddress(), passwordBuilder.toString());

            Folder[] f = store.getDefaultFolder().list();
            folderList.clear();

            ArrayList<String> folders = new ArrayList<>();

            for (Folder fd : f) {
                if (fd.getName().startsWith("[") && fd.getName().endsWith("]"))
                    continue;
                folders.add(fd.getName());
            }

            folderList.clear();
            folderList.addAll(folders);
            folderChoiceBox.getItems().addAll(folderList);
            folderChoiceBox.getSelectionModel().select(0);

            folderChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov,
                            Number old_val, Number new_val) -> currentFolder = String.valueOf(new_val));

            store.close();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //gets all emails from the validated folder, adds them to the emailTable, and prints to console
    @FXML
    private void fetchEmail(String loadFolder) {
        try {
            //first convert char[] holding password to a string builder that we can call toString on
            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < MainController.password.length; i++)
                passwordBuilder.append(MainController.password[i]);

            //init properties for smtp (Simple Mail Transfer Protocol)
            Properties props = new Properties();
            props.put("mail.smtp.auth", true); //we should authenticate
            props.put("mail.smtp.starttls.enable", true); //(tls is transport layer security and is a good practice)
            props.put("mail.smtp.host", getEmailHost(getEmailAddress())); //set host
            props.put("mail.smtp.port", 587); //port 587 is the standard SMTP port
            props.put("mail.imap.fetchsize", "4096");

            //create a session from our email (this is from our javaMail library)
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(getEmailAddress(), passwordBuilder.toString());
                        }
            });

            //imaps is simply a protocol (Internet Message Access Protocol)
            //create an imaps session using our emailAddress host, email, and password
            store = session.getStore("imaps");
            store.connect(getIMAPServer(getEmailAddress()), getEmailAddress(), passwordBuilder.toString());

            //get all the messages from the specified folder
            emailFolder = store.getFolder(folderChoiceBox.getItems().get(Integer.parseInt(currentFolder)));

            currentFolderName = folderChoiceBox.getItems().get(Integer.parseInt(currentFolder));

            emailFolder.open(Folder.READ_ONLY);
            messages = emailFolder.getMessages();

            //set how many emails we found
            Platform.runLater(() -> unreadEmailsLabel.setText(messages.length + ""));

            table.getItems().clear();

            //Add emails to table
            for (int i = messages.length - 1; i >= 0 ; i--) {
                Message message = messages[i];
                String body = getMessageText(message);
                writePart(Arrays.toString(message.getFrom()), message.getReceivedDate().toString(), message.getSubject(), body);
            }

            table.setRowFactory((tableView) -> new TooltipTableRow<>(EmailPreview::toString));
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //get the body from a Message
    public String getMessageText(Message message) {
        try {
            String result = "";

            if (message.isMimeType("text/plain"))
                result = message.getContent().toString();

            else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                result = MMText(mimeMultipart);
            }

            return result;
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    //gets the email body of a MimeMultipart message
    private String MMText(MimeMultipart mimeMulti)  {
        try {
            StringBuilder result = new StringBuilder();

            int count = mimeMulti.getCount();

            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMulti.getBodyPart(i);

                if (bodyPart.isMimeType("text/plain")) {
                    result.append("\n").append(bodyPart.getContent());

                    break; //this is necessary, do not remove
                }

                else if (bodyPart.getContent() instanceof MimeMultipart)
                    result.append(MMText((MimeMultipart) bodyPart.getContent()));
            }

            return result.toString();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    //display email messages in content emailTablet
    private void writePart(String from, String date, String subject, String message) {
        table.getItems().add(new EmailPreview(from,date,subject,message));
        table.refresh();
    }

    @FXML
    private void refreshFolder(ActionEvent event) {
        Main.startWorking("Refreshing folder",0);

        if (searchFolderField.getText().length() == 0) {
            fetchEmail(currentFolder);
            Main.startWorking("Refreshed!", 1000);
        }
    }

    //animation for going to compose screen
    @FXML
    private void gotoCompose(ActionEvent event) {
        try {
            Main.startWorking("Loading email...",0);
            composeButton.setDisable(true);
            forwardButton.setDisable(true);
            replyButton.setDisable(true);
            deleteButton.setDisable(true);
            logoutButton.setDisable(true);

            root = FXMLLoader.load(getClass().getResource("compose.fxml"));
            Scene currentScene = logoutButton.getScene();
            root.translateYProperty().set(currentScene.getHeight());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.play();

            Main.startWorking("Loaded!",1000);
            composeButton.setDisable(false);
            forwardButton.setDisable(false);
            replyButton.setDisable(false);
            deleteButton.setDisable(false);
            logoutButton.setDisable(false);
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //refined download attachments, maybe there's also some way that we can just quickly get the name's?
    public static void initAttachments() {
        try {
            currentMessageAttachments = new LinkedList<>();

            File dir = new File("temp");
            if (!dir.exists()) dir.mkdir();

            int numberOfParts = currentMessageMultipart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) currentMessageMultipart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    String fileName = part.getFileName();
                    part.saveFile(dir + System.getProperty("file.separator") + fileName);

                    currentMessageAttachments.add(new File(dir + System.getProperty("file.separator") + fileName));
                }
            }
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //view the full email, this is where the user can choose to view and download attachments if there are any
    @FXML
    private void gotoViewer(Message view) {
        try {
            EmailController.currentMessageAttachments = null;

            if (currentMessageMultipart == null)
                return;

            EmailController.currentMessage = view;
            EmailController.currentMessageMultipart = (Multipart) currentMessage.getContent();
            EmailController.currentMessageSubject = currentMessage.getSubject();
            EmailController.currentMessageFrom = String.valueOf(currentMessage.getFrom()[0]);
            EmailController.currentMessageDate = String.valueOf(currentMessage.getSentDate());
            EmailController.currentMessageBody = getMessageText(currentMessage);

            root = FXMLLoader.load(getClass().getResource("view.fxml"));
            viewRoot  = root;
            Scene currentScene = logoutButton.getScene();
            root.translateYProperty().set(currentScene.getHeight());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
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
    public void gotoReply(ActionEvent event) {
        try {
            EmailController.currentMessageAttachments = null;

            if (currentMessageMultipart == null)
                return;

            currentMessageMultipart = (Multipart) currentMessage.getContent();
            currentMessageSubject = currentMessage.getSubject();
            currentMessageFrom = String.valueOf(currentMessage.getFrom()[0]);
            currentMessageDate = String.valueOf(currentMessage.getSentDate());
            currentMessageBody = getMessageText(currentMessage);

            if (currentMessageMultipart != null) {
                root = FXMLLoader.load(EmailController.class.getResource("reply.fxml"));
                Scene currentScene = composeButton.getScene();
                root.translateXProperty().set(currentScene.getWidth());

                StackPane pc = (StackPane) currentScene.getRoot();
                pc.getChildren().add(root);

                Timeline tim = new Timeline();
                KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
                KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);

                tim.getKeyFrames().add(kf);
                tim.play();
            }
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void gotoForward(ActionEvent event) {
        try {
            EmailController.currentMessageAttachments = null;

            if (currentMessageMultipart == null)
                return;

            currentMessageMultipart = (Multipart) currentMessage.getContent();
            currentMessageSubject = currentMessage.getSubject();
            currentMessageFrom = String.valueOf(currentMessage.getFrom()[0]);
            currentMessageDate = String.valueOf(currentMessage.getSentDate());
            currentMessageBody = getMessageText(currentMessage);

            if (currentMessageMultipart != null) {
                root = FXMLLoader.load(EmailController.class.getResource("forward.fxml"));
                Scene currentScene = composeButton.getScene();
                root.translateXProperty().set(currentScene.getWidth());

                StackPane pc = (StackPane) currentScene.getRoot();
                pc.getChildren().add(root);

                Timeline tim = new Timeline();
                KeyValue kv = new KeyValue(root.translateXProperty(), 0, Interpolator.EASE_IN);
                KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);

                tim.getKeyFrames().add(kf);
                tim.play();
            }
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //animate the sliding away Inbox scene back to the main menu
    @FXML
    private void goBack(ActionEvent event) {
        try {
            //load new parent and scene
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene currentScene = logoutButton.getScene();
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

            emailFolder.close();
            store.close();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //would like to do a minimizing animation but might not have time
    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    //a general switch statement that only allows our supported email addresses
    //if we can't get yahoo or outlook working, just remove those cases
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

    //delete an email and refresh the table (from main screen)
    @FXML
    public void deleteEmail() {
        try {
            if (currentMessageMultipart == null)
                return;

            int deleteIndex = messages.length - table.getSelectionModel().getSelectedIndex() - 1;

            StringBuilder passwordBuilder = new StringBuilder();
            for (int i = 0; i < MainController.password.length; i++)
                passwordBuilder.append(MainController.password[i]);
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", getIMAPServer(getEmailAddress()));
            props.put("mail.smtp.port", 587);
            props.put("mail.imap.fetchsize", "4096");

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(getEmailAddress(), passwordBuilder.toString());
                        }
                    });

            Store store = session.getStore("imaps");
            store.connect(getIMAPServer(getEmailAddress()), getEmailAddress(), passwordBuilder.toString());

            Folder emailFolder = store.getFolder(folderChoiceBox.getItems().get(Integer.parseInt(currentFolder)));
            emailFolder.open(Folder.READ_WRITE);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            messages = emailFolder.getMessages();

            messages[deleteIndex].setFlag(Flags.Flag.DELETED, true);

            fetchEmail(currentFolder);
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void close_app(MouseEvent e) {
        if (hideOnCloseCheckBox.isSelected()) {
            Main.primaryStage.setIconified(true);
        }

        else {
            ViewController.clearLocalAttachments();
            System.exit(0);
        }

    }

    //class for table rows that helps in displaying an email preview on mouse_hover_over
    public class TooltipTableRow<T> extends TableRow<T> {

        private Function<T, String> toolTipStringFunction;

        public TooltipTableRow(Function<T, String> toolTipStringFunction) {
            this.toolTipStringFunction = toolTipStringFunction;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null) {
                setTooltip(null);
            } else {
                Tooltip tooltip = new Tooltip(toolTipStringFunction.apply(item));
                setTooltip(tooltip);
            }
        }
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
}