import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import junit.framework.TestCase;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;

import static org.testfx.api.FxAssert.verifyThat;

public class NoteViewControllerTest extends ApplicationTest {
    Stage primaryStage;

    @Override
    public void start(Stage stage){
        primaryStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("noteview.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Test
    public void testNoteViewInputFields(){
        clickOn("#noteNameField");
        write("This is the name field");
        clickOn("#noteTextArea");
        write("This the note text area for content");
        verifyThat("#noteNameField", NodeMatchers.isNotNull());
        verifyThat("#noteTextArea", NodeMatchers.isNotNull());
    }
    @Test
    public void testNoteViewButtons(){
        clickOn("#saveAndResign");
        clickOn("#discardChanges");
        clickOn("#deleteNote");
    }
}