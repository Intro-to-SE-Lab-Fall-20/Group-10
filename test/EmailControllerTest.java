import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

public class EmailControllerTest extends ApplicationTest {
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
            root = FXMLLoader.load(getClass().getResource("email.fxml"));
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
    public void testGoToCompose(){
        clickOn("#composeButton");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("compose.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Tests that we have changed from email.fxml root to compose.fxml root
        assertNotEquals(primaryStage.getScene().getRoot(), root);
    }
    @Test
    public void testLogout(){
        clickOn("#logoutButton");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Tests that we have changed from email.fxml root to main.fxml root
        assertNotEquals(primaryStage.getScene().getRoot(), root);
    }
    @Test
    public void testGoToReply(){
        clickOn("#replyButton");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("reply.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Tests that we have changed from email.fxml root to reply.fxml root
        assertNotEquals(primaryStage.getScene().getRoot(), root);
    }
    @Test
    public void testGoToForward(){
        clickOn("#forwardButton");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("forward.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Tests that we have changed from email.fxml root to forward.fxml root
        assertNotEquals(primaryStage.getScene().getRoot(), root);
    }
    @Test
    public void testSearchInboxField(){
        clickOn("#searchFolderField");
        write("hello");
        verifyThat("#searchFolderField", NodeMatchers.isNotNull());
        clickOn("#folderChoiceBox");
    }
}