package pt.uc.dei.enums;

/**
 * Enum representing different office locations.
 * <ul>
 *   <li>NO_OFFICE: No office yet attributed.</li>
 *   <li>LISBON: Office located in Lisbon, Portugal.</li>
 *   <li>COIMBRA: Office located in Coimbra, Portugal.</li>
 *   <li>OPORTO: Office located in Oporto (Porto), Portugal.</li>
 *   <li>VISEU: Office located in Viseu, Portugal.</li>
 *   <li>MUNICH: Office located in Munich, Germany.</li>
 *   <li>BOSTON: Office located in Boston, USA.</li>
 *   <li>SOUTHAMPTON: Office located in Southampton, UK.</li>
 * </ul>
 */
public enum Office {
    NO_OFFICE("noOffice"),
    LISBON("lisbon"),
    COIMBRA("coimbra"),
    OPORTO("oporto"),
    VISEU("viseu"),
    MUNICH("munich"),
    BOSTON("boston"),
    SOUTHAMPTON("southampton");

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