public class AttachementPreview {
    private String Name;
    private String Size;
    private String Type;

    AttachementPreview(String fileName, String fileSize, String fileType) {
        this.Name = fileName;
        this.Size = fileSize;
        this.Type = fileType;
    }

    //todo max lengths for getters like EmailPreview

    public void setFileName(String fileName) {
        this.Name = fileName;
    }

    public String getFileName() {
        return this.Name;
    }

    public void setFileSize(String fileSize) {
        this.Size = fileSize;
    }

    public String getFileSize() {
        return this.Size;
    }

    public void setFileType(String fileType) {
        this.Type = fileType;
    }

    public String getFileType() {
        return (this.Type).replace(".","");
    }
}
