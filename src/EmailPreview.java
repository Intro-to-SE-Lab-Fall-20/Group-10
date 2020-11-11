public class EmailPreview {
    private String From;
    private String Date;
    private String Subject;
    private String Message;

    private int maxMessageLength = 50;
    private int maxSubjectLength = 40;

    EmailPreview(String From, String Date, String Subject, String Message) {
        this.From = From;
        this.Date = Date;
        this.Subject = Subject;
        this.Message = Message;

        if (Message.length() == 0 || Message == null)
            Message = "[Null content; perhaps the email is an embedded image]";
    }

    public String getFrom() {
        if (this.From == null)
            return "[No from found]";
        return this.From.replace("[","").replace("]","").replace("<","").replace(">","");
    }

    public String getFullFrom() {
        if (this.From == null)
            return "[No from found]";
        return this.From;
    }

    public void setFrom(String from) {
        this.From = from;
    }

    public String getDate() {
        if (this.Date == null)
            return "[No send date found]";
        return Date.substring(0,16);
    }

    public String getFullDate() {
        if (this.Date == null)
            return "[No send date found]";
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getSubject() {
        if (this.Subject == null)
            return "[No subject]";
        return this.Subject.substring(0,Math.min(Subject.length(), maxSubjectLength));
    }

    public String getFullSubject() {
        if (this.Subject == null)
            return "[No subject]";
        return this.Subject;
    }

    public void setSubject(String subject) {
        this.Subject = subject;
    }

    public String getMessage() {
        if (Message.length() == 0 || Message == null)
            return "[Null content; perhaps the email is an embedded image]";
        return this.Message.substring(0,Math.min(Message.length(), maxMessageLength));
    }

    public String getFullMessage() {
        if (Message.length() == 0 || Message == null)
            return "[Null content; perhaps the email is an embedded image]";
        return this.Message;
    }

    public void setMessage(String message) {
        this.Message = message;
        if (Message.length() == 0 || Message == null)
            Message = "[Null content; perhaps the email is an embedded image]";
    }

    @Override
    public String toString() {
        return this.getFullFrom() + "\n" + this.getFullDate() + "\n" + this.getFullSubject() + "\n" + this.getFullMessage().replaceAll("(.{90})", "$1\n") + "\n";
    }
}