package pt.uc.dei.enums;

/**
 * Enum representing different professional roles in an organization.
 * - SOFTWARE_ENGINEER: General software engineering role.
 * - FRONTEND_DEVELOPER: Specializes in client-side/user interface development.
 * - BACKEND_DEVELOPER: Specializes in server-side/application logic development.
 * - FULL_STACK_DEVELOPER: Works on both frontend and backend development.
 * - DATA_SCIENTIST: Works with data analysis, machine learning, and statistics.
 * - PRODUCT_MANAGER: Oversees product development and strategy.
 * - UX_UI_DESIGNER: Focuses on user experience and interface design.
 * - DEVOPS_ENGINEER: Manages infrastructure, deployment, and operations.
 * - SYSTEM_ADMINISTRATOR: Maintains and configures computer systems.
 * - SECURITY_ANALYST: Focuses on information security and risk management.
 * - QA_ENGINEER: Ensures software quality through testing.
 * - BUSINESS_ANALYST: Bridges business needs with technical solutions.
 * - TECH_LEAD: Technical leader of a development team.
 * - CTO: Chief Technology Officer, executive-level technology leader.
 * - CEO: Chief Executive Officer, highest-ranking executive.
 * - HR_MANAGER: Manages human resources department.
 * - HR_SPECIALIST: Specializes in specific HR functions.
 * - RECRUITER: Focuses on talent acquisition and hiring.
 */
public enum Role {
    WITHOUT_ROLE("withoutRole"),
    SOFTWARE_ENGINEER("softwareEngineer"),
    FRONTEND_DEVELOPER("frontendDeveloper"),
    BACKEND_DEVELOPER("backendDeveloper"),
    FULL_STACK_DEVELOPER("fullStackDeveloper"),
    DATA_SCIENTIST("dataScientist"),
    PRODUCT_MANAGER("productManager"),
    UX_UI_DESIGNER("uxUIDesigner"),
    DEVOPS_ENGINEER("devOpsEngineer"),
    SYSTEM_ADMINISTRATOR("systemAdministrator"),
    SECURITY_ANALYST("securityAnalyst"),
    QA_ENGINEER("qaEngineer"),
    BUSINESS_ANALYST("businessAnalyst"),
    TECH_LEAD("techLead"),
    CTO("cto"),
    CEO("ceo"),
    HR_MANAGER("hrManager"),
    HR_SPECIALIST("hrSpecialist"),
    RECRUITER("recruiter"),;

    private final String fieldName;

    Role(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Role fromFieldName(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String trimmed = input.trim();
        for (Role r : values()) {
            if (r.fieldName.equalsIgnoreCase(trimmed) || (r.toString().equalsIgnoreCase(trimmed))) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown parameter field name: " + input);
    }
}