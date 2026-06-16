package hr.algebra.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StreamingPlatform {
    @JsonProperty("Netflix")
    NETFLIX("Netflix"),
    @JsonProperty("HBO Max")
    HBO_MAX("HBO Max"),
    @JsonProperty("Disney+")
    DISNEY_PLUS("Disney+"),

    @JsonProperty("Amazon Prime Video")
    AMAZON_PRIME("Amazon Prime Video"),
    @JsonProperty("Apple TV+")
    APPLE_TV_PLUS("Apple TV+"),
    @JsonProperty("Hulu")
    HULU("Hulu"),
    @JsonProperty("Paramount+")
    PARAMOUNT_PLUS("Paramount+");
    private final String name;
    StreamingPlatform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
