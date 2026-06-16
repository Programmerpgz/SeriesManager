package hr.algebra.models;

public enum Role {
    USER("User"),
    ADMINISTRATOR("Administrator");
    private final String description;
    Role(String description) {
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
