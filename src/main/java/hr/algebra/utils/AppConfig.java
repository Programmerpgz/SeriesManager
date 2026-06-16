package hr.algebra.utils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "config")
public class AppConfig {
    @JacksonXmlProperty(localName = "screenWidth")
    private double screenWidth;
    @JacksonXmlProperty(localName = "screenHeight")
    private double screenHeight;
    @JacksonXmlProperty(localName = "dbUrl")
    private String dbUrl;

    public AppConfig() {
        //treba
    }
    public double getScreenWidth() {
        return screenWidth;
    }
    public double getScreenHeight() {
        return screenHeight;
    }
    public String getDbUrl() {
        return dbUrl;
    }
}
