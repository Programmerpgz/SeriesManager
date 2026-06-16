package hr.algebra.utils;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class XmlUtils {
    private XmlUtils() {}
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final Logger log = LoggerFactory.getLogger(XmlUtils.class);

    static {
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    public static <T> void saveToXml(T entity, String filePath) {
       if(entity == null || filePath == null || filePath.isBlank()){
           return;
       }
       File file = new File(filePath);
       try {
           xmlMapper.writeValue(file, entity);
       } catch (Exception e) {
           log.error("Error writing XML: {}", e.getMessage());
       }
    }

    public static <T> T loadFromXml(String filePath, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(new File(filePath), clazz);
    }
}
