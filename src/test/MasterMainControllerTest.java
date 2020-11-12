import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.testfx.api.FxAssert.verifyThat;

public class MasterMainControllerTest extends ApplicationTest {
    public Stage primaryStage;
    private MasterMainController masterMainController = new MasterMainController();

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
            root = FXMLLoader.load(getClass().getResource("mastermain.fxml"));
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
    public void testMasterMainRegister(){
        clickOn("#registerButton");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("register.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotEquals(primaryStage.getScene().getRoot(), root);
        // asserts that there was a transfer from current scene to other scene when button pressed
    }
    @Test
    public void testMasterMainInputs(){
        StackPane rootNode = masterMainController.masterStack;
        clickOn("#usernameField");
        write("Mallory10");
        clickOn("#passwordField");
        write("mallory");
        clickOn("#loginButton");
        verifyThat("#usernameField", NodeMatchers.isNotNull());
        verifyThat("#passwordField", NodeMatchers.isNotNull());
        verifyThat("#loginButton", NodeMatchers.isNotNull());
    }
}