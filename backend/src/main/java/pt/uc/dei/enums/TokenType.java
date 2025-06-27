package pt.uc.dei.enums;

/**
 * Enum representing the type of token used in the system.
 * <ul>
 *   <li>VALIDATION: Token used for account/email validation.</li>
 *   <li>PASSWORD_RESET: Token used for password reset operations.</li>
 * </ul>
 */
public enum TokenType {
    /** Token used for account/email validation. */
    VALIDATION,
    /** Token used for password reset operations. */
    PASSWORD_RESET;
}