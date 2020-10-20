import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Flow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ComposeControllerTest extends ApplicationTest {
    static Stage primaryStage;
    Parent root;

    @Override
    public void start(Stage stage) {
        this.root = null;
        try {
            this.root = FXMLLoader.load(ComposeController.class.getResource("compose.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage = new Stage();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Test
    public void testController() {
        clickOn("#to");
        write("mnd199@msstate.edu");
        clickOn("#subject");
        write("test email compose");
        clickOn("#emailContent");
        write("Testing the composition of an email");
        clickOn("#sendButton");
        // Shows that you can compose an email
        // Get error in Controller because email password not given
    }

    @Test
    public void testAddAttachment(){
        clickOn("#to");
        write("test email");
        clickOn("#subject");
        write("testing email attachments");
        clickOn("#attachButton");
        // also get error because email password not given
        // but shows that clicking the add attachments button pulls up your file explorer
    }
}