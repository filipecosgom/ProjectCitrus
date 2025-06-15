package pt.uc.dei.enums;

/**
 * Enum representing different office locations.
 * - NO_OFFICE: no office yet atributted
 * - LISBON: Office located in Lisbon, Portugal.
 * - COIMBRA: Office located in Coimbra, Portugal.
 * - OPORTO: Office located in Oporto (Porto), Portugal.
 * - VISEU: Office located in Viseu, Portugal.
 * - MUNICH: Office located in Munich, Germany.
 * - BOSTON: Office located in Boston, USA.
 * - SOUTHAMPTON: Office located in Southampton, UK.
 */
public enum Office {
    NO_OFFICE("noOffice"),
    LISBON("lisbon"),
    COIMBRA("coimbra"),
    OPORTO("oporto"),
    VISEU("viseu"),
    MUNICH("munich"),
    BOSTON("boston"),
    SOUTHAMPTON("southampton"),;

    private final String fieldName;

    Office(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Office fromFieldName(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String trimmed = input.trim();
        for (Office o : values()) {
            if (o.fieldName.equalsIgnoreCase(trimmed) || (o.toString().equalsIgnoreCase(trimmed))) {
                return o;
            }
        }
        throw new IllegalArgumentException("Unknown parameter field name: " + input);
    }
}