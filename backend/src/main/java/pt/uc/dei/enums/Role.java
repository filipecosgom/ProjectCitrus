package pt.uc.dei.enums;

/**
 * Enum representing different professional roles in an organization.
 * <ul>
 *   <li>WITHOUT_ROLE: No role assigned.</li>
 *   <li>SOFTWARE_ENGINEER: General software engineering role.</li>
 *   <li>FRONTEND_DEVELOPER: Specializes in client-side/user interface development.</li>
 *   <li>BACKEND_DEVELOPER: Specializes in server-side/application logic development.</li>
 *   <li>FULL_STACK_DEVELOPER: Works on both frontend and backend development.</li>
 *   <li>DATA_SCIENTIST: Works with data analysis, machine learning, and statistics.</li>
 *   <li>PRODUCT_MANAGER: Oversees product development and strategy.</li>
 *   <li>UX_UI_DESIGNER: Focuses on user experience and interface design.</li>
 *   <li>DEVOPS_ENGINEER: Manages infrastructure, deployment, and operations.</li>
 *   <li>SYSTEM_ADMINISTRATOR: Maintains and configures computer systems.</li>
 *   <li>SECURITY_ANALYST: Focuses on information security and risk management.</li>
 *   <li>QA_ENGINEER: Ensures software quality through testing.</li>
 *   <li>BUSINESS_ANALYST: Bridges business needs with technical solutions.</li>
 *   <li>TECH_LEAD: Technical leader of a development team.</li>
 *   <li>CTO: Chief Technology Officer, executive-level technology leader.</li>
 *   <li>CEO: Chief Executive Officer, highest-ranking executive.</li>
 *   <li>HR_MANAGER: Manages human resources department.</li>
 *   <li>HR_SPECIALIST: Specializes in specific HR functions.</li>
 *   <li>RECRUITER: Focuses on talent acquisition and hiring.</li>
 * </ul>
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