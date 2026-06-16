package hr.algebra.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "seriesLibrary")
public class SeriesWrapper {
    @JacksonXmlProperty(localName = "series")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Series> series;

    public SeriesWrapper() {
    }
    public SeriesWrapper(List<Series> series) {
        this.series = series;
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }
}