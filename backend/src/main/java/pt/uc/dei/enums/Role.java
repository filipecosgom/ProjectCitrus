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
    WITHOUT_ROLE,
    SOFTWARE_ENGINEER,
    FRONTEND_DEVELOPER,
    BACKEND_DEVELOPER,
    FULL_STACK_DEVELOPER,
    DATA_SCIENTIST,
    PRODUCT_MANAGER,
    UX_UI_DESIGNER,
    DEVOPS_ENGINEER,
    SYSTEM_ADMINISTRATOR,
    SECURITY_ANALYST,
    QA_ENGINEER,
    BUSINESS_ANALYST,
    TECH_LEAD,
    CTO,
    CEO,
    HR_MANAGER,
    HR_SPECIALIST,
    RECRUITER;
}