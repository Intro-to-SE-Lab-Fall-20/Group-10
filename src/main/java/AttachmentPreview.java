import java.io.File;

public class AttachmentPreview {
    private String Name;
    private String Size;
    private String Type;

    private File pointerFile;

    AttachmentPreview(String Name, String Size, String Type, File pointerFile) {
        this.Name = Name;
        this.Size = Size;
        this.Type = Type;
        this.pointerFile = pointerFile;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return this.Name.substring(0, Math.min(40,Name.length()));
    }

    public void setSize(String Size) {
        this.Size = Size;
    }

    public String getSize() {
        return this.Size;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getType() {
        return (this.Type).replace(".","");
    }

    public void setPointerFile(File pointerFile) {
        this.pointerFile = pointerFile;
    }

    public File getPointerFile() {
        return this.pointerFile;
    }

    @Override
    public String toString() {
        return this.Name + "\n" + this.getSize() + "\n" + this.getType();
    }
}
