package hr.algebra.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.LocalDateTime;

@JacksonXmlRootElement(localName = "LogEntry")
public class LogEntry {
    @JacksonXmlProperty(localName = "timestamp")
    private String timestamp;
    @JacksonXmlProperty(localName = "user")
    private String user;
    @JacksonXmlProperty(localName = "action")
    private String action;
    @JacksonXmlProperty(localName = "level")
    private String level;

    public LogEntry() {}
    public LogEntry(String user, String action, String level) {
        this.timestamp = LocalDateTime.now().toString();
        this.user = user;
        this.action = action;
        this.level = level;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s: %s", timestamp, level, user, action);
    }

}
