package hr.algebra.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "userLogs")
public class UserLogWrapper {
    @JacksonXmlProperty(localName = "log")
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<LogEntry> logs = new ArrayList<>();

    public UserLogWrapper() {
        //potreban
    }
    public void addLog(LogEntry entry) {
        if(entry != null) {
            this.logs.add(entry);
        }
    }
}
