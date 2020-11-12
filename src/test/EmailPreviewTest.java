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

public class EmailPreviewTest extends ApplicationTest {
    public EmailPreview emailPreview;
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
       emailPreview = new EmailPreview("Mallory","10-23","test","this is a test");
    }
    @Test
    public void testEmailPreview(){
        assert emailPreview.getFrom().equals("Mallory");
        assert emailPreview.getFullDate().equals("10-23");
        assert emailPreview.getSubject().equals("test");
        assert emailPreview.getMessage().equals("this is a test");
    }
}