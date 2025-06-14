package pt.uc.dei.enums;

/**
 * Enum representing different parameters for sorting or filtering.
 * - FIRST_NAME: Represents a user's first name.
 * - SURNAME: Represents a user's surname.
 * - EMAIL: Represents a user's email address.
 * - DATE: Represents a date-related parameter.
 */
public enum Parameter {
    FIRST_NAME("name"),
    SURNAME("surname"),
    EMAIL("email"),
    DATE("creationDate");

    private final String fieldName;

    Parameter(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}