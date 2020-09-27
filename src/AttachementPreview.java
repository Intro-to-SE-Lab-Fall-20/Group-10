public class AttachementPreview {
    private String Name;
    private String Size;
    private String Type;

    AttachementPreview(String Name, String Size, String Type) {
        this.Name = Name;
        this.Size = Size;
        this.Type = Type;
    }

    //todo max lengths for getters like EmailPreview

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return this.Name;
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

    @Override
    public String toString() {
        return this.getName() + " : " + this.getSize() + " : " + this.getType();
    }
}
