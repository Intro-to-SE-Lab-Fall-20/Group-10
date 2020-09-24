public class EmailPreview {
    private String From;
    private String Date;
    private String Subject;
    private String Message;

    EmailPreview(String From, String Date, String Subject, String Message) {
        this.From = From;
        this.Date = Date;
        this.Subject = Subject;
        this.Message = Message;
    }

    public String getFrom() {
        return this.From;
    }

    public void setFrom(String from) {
        this.From = from;
    }

    public String getDate() {
        return this.Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getSubject() {
        return this.Subject;
    }

    public void setSubject(String subject) {
        this.Subject = subject;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }
}
