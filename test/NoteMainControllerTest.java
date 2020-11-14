import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

import static org.junit.Assert.assertNotEquals;

public class NoteMainControllerTest extends ApplicationTest {
    public Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("notemain.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Test
    public void testButtons(){
        clickOn("#addNote");
        clickOn("#deleteNote");
        clickOn("#openNote");
    }

    @Test
    public void testBackButton(){
        clickOn("#backButton");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotEquals(primaryStage.getScene().getRoot(), root);
    }

}