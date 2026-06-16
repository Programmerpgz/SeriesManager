package hr.algebra.utils;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import hr.algebra.models.LogEntry;
import hr.algebra.models.UserLogWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public final class XmlActivityLogger {
    private static final Logger log = LoggerFactory.getLogger(XmlActivityLogger.class);
    private static final String FILE_PATH = "user_activity.xml";
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    private XmlActivityLogger() {}
    public static synchronized void logActivity(String user, String action, String level) {
        try {
            File file = new File(FILE_PATH);
            UserLogWrapper userLogWrapper;

            if (file.exists() && file.length() > 0) {
                userLogWrapper = xmlMapper.readValue(file, UserLogWrapper.class);
            } else {
                userLogWrapper = new UserLogWrapper();
            }

            LogEntry newEntry = new LogEntry(user, action, level);
            userLogWrapper.addLog(newEntry);
            xmlMapper.writeValue(file, userLogWrapper);

        } catch (IOException e) {
            log.error("Failed while trying to log activity to XML file.", e);
        }
    }
}
