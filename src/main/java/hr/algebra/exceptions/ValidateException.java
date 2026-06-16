package hr.algebra.exceptions;

public class ValidateException extends Exception {
    @SuppressWarnings("unused")
    private final String field;
    public ValidateException(String field, String message) {
        super("Validation for field: " + field + " : " + message);
        this.field = field;
    }
    @SuppressWarnings("unused")
    public String getField() {
        return field;
    }
}
