package pt.uc.dei.enums;

/**
 * Enum representing different parameters for sorting or filtering.
 * <ul>
 *   <li>ID: Unique identifier.</li>
 *   <li>FIRST_NAME: User's first name.</li>
 *   <li>SURNAME: User's surname.</li>
 *   <li>EMAIL: User's email address.</li>
 *   <li>DATE: Date-related parameter.</li>
 *   <li>ROLE: User's role.</li>
 *   <li>OFFICE: User's office location.</li>
 *   <li>MANAGER: User's manager name.</li>
 * </ul>
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