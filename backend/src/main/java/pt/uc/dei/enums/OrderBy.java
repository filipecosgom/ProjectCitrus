package pt.uc.dei.enums;

/**
 * Enum representing sorting order options.
 * <ul>
 *   <li>ASCENDING: Sorts elements from smallest to largest.</li>
 *   <li>DESCENDING: Sorts elements from largest to smallest.</li>
 * </ul>
 */
public enum Order {
    ASCENDING("ascending"),
    DESCENDING("descending");

    private final String fieldName;

    Order(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Order fromFieldName(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String trimmed = input.trim();
        for (Order o : values()) {
            if (o.fieldName.equalsIgnoreCase(trimmed) || (o.toString().equalsIgnoreCase(trimmed))) {
                return o;
            }
        }
        throw new IllegalArgumentException("Unknown parameter field name: " + input);
    }

}