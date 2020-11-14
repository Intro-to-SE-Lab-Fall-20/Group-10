import javafx.stage.Stage;
import junit.framework.TestCase;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class NotePreviewTest extends ApplicationTest {
    NotePreview notePreview;
    @Override
    public void start(Stage stage){
        notePreview = new NotePreview("Test", "This is the test note content");
    }
    @Test
    public void testNotePreview(){
        assert notePreview.getName().equals("Test");
        assert notePreview.getContent().equals("This is the test note content");
    }
}