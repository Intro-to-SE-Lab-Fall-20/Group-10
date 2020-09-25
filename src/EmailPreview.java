import java.text.SimpleDateFormat;

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
    }

    public String getFrom() {
        return this.From.replace("[","").replace("]","");
    }

    public void setFrom(String from) {
        this.From = from;
    }

    public String getDate() {
        return Date.substring(0,16);
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getSubject() {
        return this.Subject.substring(0,Math.min(Subject.length(), maxSubjectLength));
    }

    public void setSubject(String subject) {
        this.Subject = subject;
    }

    public String getMessage() {
        return this.Message.substring(0,Math.min(Message.length(), maxMessageLength));
    }

    public void setMessage(String message) {
        this.Message = message;
    }
}
