import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class AttachmentPreviewTest extends ApplicationTest {
    public AttachmentPreview attachmentPreview;
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
        try {
            attachmentPreview = new AttachmentPreview("attach name", "345","image", new File(getClass().getResource("classDiagram/classDiagram.pdf").toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testEmailPreview() throws URISyntaxException {
        assert attachmentPreview.getName().equals("attach name");
        assert attachmentPreview.getSize().equals("345");
        assert attachmentPreview.getType().equals("image");
        assert attachmentPreview.getPointerFile().equals(new File(getClass().getResource("classDiagram/classDiagram.pdf").toURI()));
    }

}