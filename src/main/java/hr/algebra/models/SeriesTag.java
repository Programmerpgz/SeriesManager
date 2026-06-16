package hr.algebra.models;

import java.util.Optional;

public enum SeriesTag {
    LIBRARY("seriesLibrary"),
    SERIES("series"),
    NAME("name"),
    DESCRIPTION("description"),
    SEASONS("numberOfSeasons"),
    EPISODES("numberOfEpisodes"),
    RELEASE_YEAR("yearOfRelease"),
    END_YEAR("endYear"),
    POSTER("posterPath"),
    GENRES("genres"),
    GENRE("genre"),
    PLATFORMS("platforms"),
    PLATFORM("platform"),
    DIRECTOR("director"),
    ACTORS("actors"),
    ACTOR("actor"),
    SURNAME("surname"),
    YEAR_OF_BIRTH("yearOfBirth"),
    NATIONALITY("nationality"),
    BIOGRAPHY("biography");

    private final String tagName;
    SeriesTag(String tagName) {
        this.tagName = tagName;
    }

    public static Optional<SeriesTag> from(String text) {
        for (SeriesTag tag : values()) {
            if (tag.tagName.equalsIgnoreCase(text)) {
                return Optional.of(tag);
            }
        }
        return Optional.empty();
    }
}
