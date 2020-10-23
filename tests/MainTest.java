import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

public class MainTest extends ApplicationTest {
    // Tests Controller too
    public Stage primaryStage;
    public double xOffset = 0d;
    public double yOffset = 0d;
    @Before
    public void setUp () throws Exception {
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start(Stage stage){
        primaryStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("IO/Logo.png")));

        //allow the primary stage to be movable
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
            primaryStage.setOpacity(0.8f);
        });

        root.setOnDragDone((event -> primaryStage.setOpacity(1.0f)));
        root.setOnMouseReleased((event -> primaryStage.setOpacity(1.0f)));

        primaryStage.show();
    }
    @Test
    public void testLoginInputFields(){
        clickOn("#emailField");
        write("straightshotmsu@gmail.com");
        clickOn("#passField");
        write("password");
        verifyThat("#emailField", NodeMatchers.isNotNull());
        verifyThat("#passField", NodeMatchers.isNotNull());
    }
    @Test
    public void testInterfaceDrag(){
        drag(primaryStage); drag(primaryStage);
        assertNotEquals(xOffset, 0d);
        assertNotEquals(yOffset, 0d);
    }
}