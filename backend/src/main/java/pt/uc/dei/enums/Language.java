package pt.uc.dei.enums;

/**
 * Enum representing the available languages for courses.
 * <ul>
 *   <li>PORTUGUESE: Course is conducted in Portuguese.</li>
 *   <li>ENGLISH: Course is conducted in English.</li>
 *   <li>ITALIAN: Course is conducted in Italian.</li>
 *   <li>FRENCH: Course is conducted in French.</li>
 *   <li>SPANISH: Course is conducted in Spanish.</li>
 * </ul>
 */
public enum Language {
    PORTUGUESE("pt"),
    ENGLISH("en"),
    ITALIAN("it"),
    FRENCH("fr"),
    SPANISH("es");

    private final String fieldName;

    Language(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Language fromFieldName(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String trimmed = input.trim();
        for (Language l : values()) {
            if (l.fieldName.equalsIgnoreCase(trimmed) || (l.toString().equalsIgnoreCase(trimmed))) {
                return l;
            }
        }
        throw new IllegalArgumentException("Unknown parameter field name: " + input);
    }
}