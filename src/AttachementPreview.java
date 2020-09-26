public class AttachementPreview {
    private String name;
    private String size;
    private String type;

    AttachementPreview(String fileName, String fileSize, String fileType) {
        this.name = fileName;
        this.size = fileSize;
        this.type = fileType;
    }

    //todo max lengths for getters like EmailPreview

    public void setFileName(String fileName) {
        this.name = fileName;
    }

    public String getFileName() {
        return this.name;
    }

    public void setFileSize(String fileSize) {
        this.size = fileSize;
    }

    public String getFileSize() {
        return this.size;
    }

    public void setFileType(String fileType) {
        this.type = fileType;
    }

    public String getFileType() {
        return this.type;
    }
}
