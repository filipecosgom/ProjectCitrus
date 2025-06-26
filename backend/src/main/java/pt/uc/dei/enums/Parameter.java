package pt.uc.dei.enums;

/**
 * Enum representing different parameters for sorting or filtering.
 * - FIRST_NAME: Represents a user's first name.
 * - SURNAME: Represents a user's surname.
 * - EMAIL: Represents a user's email address.
 * - DATE: Represents a date-related parameter.
 */
public enum Parameter {
    ID("id"),
    FIRST_NAME("name"),
    SURNAME("surname"),
    EMAIL("email"),
    DATE("creationDate"),
    ROLE("role"),
    OFFICE("office"),
    MANAGER("manager.name");

    private final String fieldName;

    Parameter(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Parameter fromFieldName(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String trimmed = input.trim();
        for (Parameter p : values()) {
            if (p.fieldName.equalsIgnoreCase(trimmed) || (p.toString().equalsIgnoreCase(trimmed))) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown parameter field name: " + input);
    }

}