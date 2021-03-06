import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

public class ReplyControllerTest extends ApplicationTest {
    public Stage primaryStage;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start(Stage stage){
        primaryStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("reply.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("IO/Logo.png")));
        primaryStage.show();
    }
    @Test
    public void testReplyInputFields(){
        clickOn("#replyTo");
        write("mnd199@msstate.edu");
        clickOn("#replySubject").doubleClickOn("#replySubject").eraseText(0);
        write("Testing for reply");
        clickOn("#emailContent").doubleClickOn("#emailContent").eraseText(0);
        write("This is the reply test content");
        verifyThat("#replyTo", NodeMatchers.isNotNull());
        verifyThat("#replySubject", NodeMatchers.isNotNull());
        verifyThat("#emailContent", NodeMatchers.isNotNull());
    }
    @Test
    public void testGoBack(){
        clickOn("#discardButton");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotEquals(primaryStage.getScene().getRoot(), root);
    }
    @Test
    public void testAddAttachments(){
        clickOn("#attachButton");
        ObservableList<Window> open = Stage.getWindows();
        // Verifies that the attachment window pops up
        assertNotEquals(open.size(), 0);
    }
}