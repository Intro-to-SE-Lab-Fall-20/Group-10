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

public class RegisterControllerTest extends ApplicationTest {
    Stage primaryStage;

    @Override
    public void start(Stage stage){
        primaryStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("register.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("StraightShot");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Test
    public void testRegisterControllerInputFields(){
        clickOn("#newusernameField");
        write("Mallory24");
        clickOn("#passField");
        write("mallory");
        clickOn("#confPassword");
        write("mallory");
        verifyThat("#newusernameField", NodeMatchers.isNotNull());
        verifyThat("#passField", NodeMatchers.isNotNull());
        verifyThat("#confPassword", NodeMatchers.isNotNull());
    }

    @Test
    public void testRegisterControllerLoginButton(){
        clickOn("#registerLogin");
    }
}