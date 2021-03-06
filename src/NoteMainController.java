import javafx.animation.*;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.function.Function;

public class NoteMainController {

    @FXML public TableView<NotePreview> table;
    @FXML public TableColumn noteCol;

    public File[] noteFiles;
    public static File currentFile;
    public static String currentName;
    public static String currentContents;
    public Button addNote ;
    public Button backButton;
    public Button deleteNote;
    public Button openNote;

    @FXML
    private void initialize() {
        noteCol.setCellValueFactory(new PropertyValueFactory<NotePreview, String>("name"));

        table.setColumnResizePolicy((param) -> true );

        //don't let the user rearrange the column ordering
        table.getColumns().addListener((ListChangeListener) change -> {
            change.next();
            if(change.wasReplaced()) {
                table.getColumns().clear();
                table.getColumns().addAll(noteCol);
            }
        });

        noteCol.setCellFactory(new Callback<TableColumn<NotePreview,String>, TableCell<NotePreview,String>>() {
            @Override
            public TableCell<NotePreview, String> call( TableColumn<NotePreview, String> param) {
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

        //open the email on mouse click if we can
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                try {
                    int index = table.getSelectionModel().getSelectedIndex();
                    if (index >= 0 && index < noteFiles.length)
                        currentFile = noteFiles[index];
                }

                catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (event.getClickCount() > 1) {
                try {
                    int index = table.getSelectionModel().getSelectedIndex();

                    if (index >= 0 && index < noteFiles.length) {
                        currentFile = noteFiles[index];
                        currentName = currentFile.getName();
                        currentContents = getContents(currentFile);
                        openNoteAction(null);
                    }

                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        table.getItems().clear();

        refreshNoteFiles();

        if (noteFiles != null)
            for (File f : noteFiles)
                writePart(f.getName(),getContents(f));

        table.setRowFactory((tableView) -> new TooltipTableRow<>(NotePreview::toString));
    }

    private String getContents(File f) {
        if (f.isDirectory())
            return "INVALID FILE : DIRECTORY";
        else if (!f.getName().endsWith(".txt"))
            return "INVALID FILE : NOT A NOTE";
        else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = br.readLine();
                StringBuilder build = new StringBuilder();

                while (line != null) {
                    build.append(line);
                    build.append("\n");
                    line = br.readLine();
                }

                br.close();

                return build.toString();
            }

            catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return "INVALID FILE : UNKNOWN";
    }

    @FXML
    public void back(ActionEvent e) {
        try {
            //load new parent and scene
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene currentScene = table.getScene();
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
            tim.setOnFinished(event1 -> pc.getChildren().remove(MainController.root));
            tim.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refreshNoteFiles() {
        File currentUserFolder = new File("users/" + MasterMainController.currentUser);

        if (!currentUserFolder.exists()) {
            currentUserFolder.mkdirs();
            noteFiles = null;
            return;
        }

        LinkedList<File> validFiles = new LinkedList<>();

        for (File f : currentUserFolder.listFiles())
            if (f.getName().endsWith(".txt"))
                validFiles.add(f);

        noteFiles = validFiles.toArray(new File[0]);
    }

    @FXML
    public void addNoteAction(ActionEvent e) {
        try {
            File users = new File("users");
            if (!users.exists()) users.mkdir();

            refreshNoteFiles();

            String validName = "untitled";
            File[] takenFiles = noteFiles;
            LinkedList<String> takenNames = new LinkedList<>();
            int i = 1;

            for (File f : takenFiles)
                takenNames.add(f.getName().replace(".txt",""));

            while (takenNames.contains(validName + i)) {
                i++;
            }

            File mkFile = new File("users/" + MasterMainController.currentUser + "/" + validName +  i + ".txt");
            mkFile.createNewFile();

            currentFile = mkFile;
            currentName = currentFile.getName();

            openNoteAction(e);

            table.getItems().clear();

            refreshNoteFiles();

            for (File f : noteFiles)
                writePart(f.getName(),getContents(f));

            table.setRowFactory((tableView) -> new TooltipTableRow<>(NotePreview::toString));
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void openNoteAction(ActionEvent e) {
        if (currentFile == null) {
            return;
        }

        currentName = currentFile.getName();
        currentContents = getContents(currentFile);

        try {
            Parent removeRoot = MasterMainController.root;
            MasterMainController.root = FXMLLoader.load(getClass().getResource("noteview.fxml"));
            Scene currentScene = table.getScene();
            MasterMainController.root.translateYProperty().set(currentScene.getHeight());

            StackPane pc = (StackPane) currentScene.getRoot();
            pc.getChildren().add(MasterMainController.root);

            Timeline tim = new Timeline();
            KeyValue kv = new KeyValue(MasterMainController.root.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(0.5), kv);
            tim.getKeyFrames().add(kf);
            tim.setOnFinished(event1 -> pc.getChildren().remove(removeRoot));
            tim.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void deleteNoteAction(ActionEvent e) {
        int delIndex = table.getSelectionModel().getSelectedIndex();

        if (delIndex >= 0 && delIndex < table.getItems().size()) {
            currentFile = noteFiles[delIndex];

            try {
                currentFile.delete();
                showPopupMessage(currentFile.getName() + " was successfully deleted",Main.primaryStage);
            }

            catch (Exception ex) {
                showPopupMessage("Could not delete " + currentFile.getName() ,Main.primaryStage);
                ex.printStackTrace();
            }

            table.getItems().clear();

            refreshNoteFiles();

            for (File f : noteFiles)
                writePart(f.getName(),getContents(f));

            table.setRowFactory((tableView) -> new TooltipTableRow<>(NotePreview::toString));
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
            popup.setY(stage.getY() + 20);
        });
        popup.show(stage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    //display email messages in content emailTable
    private void writePart(String name, String content) {
        table.getItems().add(new NotePreview(name,content));
        table.refresh();
    }

    //class for table rows that helps in displaying a notepreview on mouse_hover_over
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
}
