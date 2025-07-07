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
public enum CourseParameter {
    ID("id"),
    TITLE("title"),
    DATE("date"),
    DURATION("duration"),
    LANGUAGE("language"),
    AREA("area"),
    DESCRIPTION("description"),
    ADMIN("admin.name");

    private final String fieldName;

    CourseParameter(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static CourseParameter fromFieldName(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String trimmed = input.trim();
        for (CourseParameter p : values()) {
            if (p.fieldName.equalsIgnoreCase(trimmed) || (p.toString().equalsIgnoreCase(trimmed))) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown parameter field name: " + input);
    }

}