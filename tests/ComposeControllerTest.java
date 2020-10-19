import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Flow;

import static org.junit.Assert.*;

public class ComposeControllerTest extends ApplicationTest {
    static Stage primaryStage;
    ComposeController controller;

   @Override
    public void start(Stage stage){
       Parent root = null;
       try {
           root = FXMLLoader.load(ComposeController.class.getResource("compose.fxml"));
       } catch (IOException e) {
           e.printStackTrace();
       }
       primaryStage = new Stage();
       primaryStage.setScene(new Scene(root));
       primaryStage.show();

   }

   @Test
    public void testController(){
       clickOn("#to");
       write("someone");
   }
}