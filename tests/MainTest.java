import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class MainTest extends ApplicationTest {
    static Stage primaryStage;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage){
        Parent root = null;
        try {
            root = FXMLLoader.load(Main.class.getResource("Main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage = new Stage();
        primaryStage.setScene(new Scene(root));

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

    @Before
    public void setUp(){

    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void testInput () {
        // test that a user is able to input text into the text fields
        clickOn("#emailField").doubleClickOn("#emailField").eraseText(0).eraseText(1);
        write("straightshotmsu@gmail.com");
        clickOn("#passField");
        write("password");
    }

    @Test
    public void testDrag(){
        // test the dragging of primary stage to ensure our drag offset values work
        drag(primaryStage);
        drag(primaryStage);
        assertThat(xOffset, not(0));
        assertThat(yOffset, not(0));
    }

}