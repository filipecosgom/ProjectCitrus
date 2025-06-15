package pt.uc.dei.enums;

/**
 * Enum representing sorting order options.
 * - ASCENDING: Sorts elements from smallest to largest.
 * - DESCENDING: Sorts elements from largest to smallest.
 */
public enum Order {
    ASCENDING("name"),
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