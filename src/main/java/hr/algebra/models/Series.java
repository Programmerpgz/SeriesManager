package hr.algebra.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JacksonXmlRootElement(localName = "series")
public class Series extends BaseEntity implements Comparable<Series>{
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "numberOfSeasons")
    private int numberOfSeasons;
    @JacksonXmlProperty(localName = "numberOfEpisodes")
    private int numberOfEpisodes;
    @JacksonXmlProperty(localName = "yearOfRelease")
    private int yearOfRelease;
    @JacksonXmlProperty(localName = "endYear")
    private Integer endYear;
    @JacksonXmlProperty(localName = "description")
    private String description;
    @JacksonXmlProperty(localName = "posterPath")
    private String posterPath;
    @JacksonXmlProperty(localName = "director")
    private Director director;
    @JacksonXmlProperty(localName = "actor")
    @JacksonXmlElementWrapper(localName = "actors")
    private List<Actor> actors;
    @JacksonXmlProperty(localName = "genre")
    @JacksonXmlElementWrapper(localName = "genres")
    private List<Genre> genres;
    @JacksonXmlProperty(localName = "platform")
    @JacksonXmlElementWrapper(localName = "platforms")
    private List<StreamingPlatform> streamingPlatforms;

    @JsonIgnore
    private String genreString;
    @JsonIgnore
    private String platformString;

    public void setGenreString(String genreString) {
        this.genreString = genreString;
    }
    public void setPlatformString(String platformString) {
        this.platformString = platformString;
    }

    public Series(String name, int numberOfSeasons, int numberOfEpisodes, int yearOfRelease, Integer endYear, String description) {
        super(0);
        this.name = name;
        this.numberOfSeasons = numberOfSeasons;
        this.numberOfEpisodes = numberOfEpisodes;
        this.yearOfRelease = yearOfRelease;
        this.endYear = endYear;
        this.description = description;
        this.actors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.streamingPlatforms = new ArrayList<>();
    }

    public Series(int id, String name, int numberOfSeasons, int numberOfEpisodes, int yearOfRelease, Integer endYear, String description){
        super(id);
        this.name = name;
        this.numberOfSeasons = numberOfSeasons;
        this.numberOfEpisodes = numberOfEpisodes;
        this.yearOfRelease = yearOfRelease;
        this.endYear = endYear;
        this.description = description;
        this.actors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.streamingPlatforms = new ArrayList<>();
    }

    public Series() {
        super(0);
        this.actors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.streamingPlatforms = new ArrayList<>();
    }
    public boolean addActor(Actor actor){
        Objects.requireNonNull(actor, "Actor cannot be null!");
        return actors.add(actor);

    }
    public void addGenre(Genre genre){
        genres.add(genre);
    }
    public void addStreamingPlatform(StreamingPlatform streamingPlatform){
        streamingPlatforms.add(streamingPlatform);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }
    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }
    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }
    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }
    public int getYearOfRelease() {
        return yearOfRelease;
    }
    public void setYearOfRelease(int yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }
    public Integer getEndYear() {
        return endYear;
    }
    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        if(posterPath == null || posterPath.trim().isEmpty()){
            this.posterPath = "assets/1775827426087_noimageavaliable.jpg";
        }else {
            this.posterPath = posterPath;
        }
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
    public void setStreamingPlatforms(List<StreamingPlatform> streamingPlatforms) {
        this.streamingPlatforms = streamingPlatforms;
    }

    public List<Actor> getActors() {
        return actors;
    }
    public List<Genre> getGenres() {
        return genres;
    }
    public List<StreamingPlatform> getStreamingPlatforms() {
        return streamingPlatforms;
    }
    public String getEmittingPeriod() {
        if (endYear == null) {
            return yearOfRelease + " - today";
        }
        return yearOfRelease + " - " + endYear;
    }

    @Override
    public int compareTo(Series series) {
        return this.name.compareToIgnoreCase(series.name);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return  "Series{id=" + getId() +
                ", name ='" + name + '\'' +
                ", season =" + numberOfSeasons +
                ", episode =" + numberOfEpisodes +
                ", periodOfEmitting ='" + getEmittingPeriod() + '\'' +
                ", genres =" + genres +
                ", streaming platforms =" + streamingPlatforms +
                ", actors =" + actors.size() + '}';
    }
}
