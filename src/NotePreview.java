public class NotePreview {
    private String name;
    private String content;

    public NotePreview(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return this.getName() + "\n\n" + this.getContent() + "\n";
    }
}
