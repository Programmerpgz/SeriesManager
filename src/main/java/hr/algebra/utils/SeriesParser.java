package hr.algebra.utils;

import hr.algebra.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SeriesParser {
    private static final Logger log = LoggerFactory.getLogger(SeriesParser.class);
    private SeriesParser() {}
    public static List<Series> parse(String source) {
        List<Series> seriesList = new ArrayList<>();

        if (source == null || source.isBlank()) {
            log.warn("Source for XML parser is empty or null!");
            return seriesList;
        }

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try (InputStream inputStream = createInputStream(source)) {
            SeriesWrapper seriesWrapper = xmlMapper.readValue(inputStream, SeriesWrapper.class);
            seriesList = seriesWrapper.getSeries();
        } catch (IOException e) {
            log.error("Failed while trying to parse data from source: {}", source, e);
        }

        return seriesList;
    }

    private static InputStream createInputStream(String source) throws IOException {
        if (source.startsWith("http")) {
            URL url = URI.create(source).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return connection.getInputStream();
        } else {
            return new FileInputStream(source);
        }
    }
}