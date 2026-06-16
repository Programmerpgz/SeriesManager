package hr.algebra.models;

import com.fasterxml.jackson.annotation.JsonProperty;
public enum Genre {
    @JsonProperty("Drama")
    DRAMA("Drama"),
    @JsonProperty("Comedy")
    COMEDY("Comedy"),
    @JsonProperty("Thriller")
    THRILLER("Thriller"),
    @JsonProperty("Sci_fi")
    SCI_FI("Sci_fi"),
    @JsonProperty("Action")
    ACTION("Action"),
    @JsonProperty("Horror")
    HORROR("Horror"),
    @JsonProperty("Documentary")
    DOCUMENTARY("Documentary"),
    @JsonProperty("Animated")
    ANIMATED("Animated"),
    @JsonProperty("Crime")
    CRIME("Crime"),
    @JsonProperty("Romance")
    ROMANCE("Romance");
    private final String description;
    Genre(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
