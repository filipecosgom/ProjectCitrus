package pt.uc.dei.enums;

public enum AppraisalParameter {
    ID("id"),
    CREATION_DATE("creationDate"),
    END_DATE("endDate"),
    SCORE("score"),
    STATE("state"),
    APPRAISED_NAME("appraisedUser.name"),
    APPRAISED_EMAIL("appraisedUser.email"),
    MANAGER_NAME("appraisingUser.name"),
    MANAGER_EMAIL("appraisingUser.email");

    private final String fieldName;

    AppraisalParameter(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static AppraisalParameter fromFieldName(String name) {
        for (AppraisalParameter param : values()) {
            if (param.name().equalsIgnoreCase(name) || param.getFieldName().equalsIgnoreCase(name)) {
                return param;
            }
        }
        return null;
    }
}