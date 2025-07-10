package pt.uc.dei.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing different areas of a course.
 * <ul>
 *   <li>FRONTEND: Covers topics related to user interface and client-side development.</li>
 *   <li>BACKEND: Focuses on server-side logic and database management.</li>
 *   <li>INFRASTRUCTURE: Deals with system architecture, networking, and cloud services.</li>
 *   <li>UX_UI: Encompasses user experience and user interface design principles.</li>
 * </ul>
 */
public enum CourseArea {
    FRONTEND("frontend"),
    BACKEND("backend"),
    INFRASTRUCTURE("infrastructure"),
    UX_UI("ux_ui");

    private final String fieldName;

    CourseArea(String fieldName) {
        this.fieldName = fieldName;
    }

    @JsonValue
    public String getFieldName() {
        return fieldName;
    }

    @JsonCreator
    public static CourseArea fromFieldName(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String trimmed = input.trim();
        for (CourseArea l : values()) {
            if (l.fieldName.equalsIgnoreCase(trimmed) || (l.toString().equalsIgnoreCase(trimmed))) {
                return l;
            }
        }
        throw new IllegalArgumentException("Unknown parameter field name: " + input);
    }

    @Override
    public String toString() {
        return fieldName;
    }
}