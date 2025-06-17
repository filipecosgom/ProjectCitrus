package pt.uc.dei.dtos;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a password reset token.
 * Stores token details associated with a user.
 */
public class PasswordResetTokenDTO {

    /**
     * The unique identifier for the password reset token.
     */
    private Long id;

    /**
     * The value of the password reset token.
     * Used for resetting user passwords.
     */
    private String tokenValue;

    /**
     * The creation date and time of the password reset token.
     */
    private LocalDateTime creationDate;

    /**
     * The user associated with this password reset token.
     */
    private UserDTO user;

    public PasswordResetTokenDTO() {
    }

    public PasswordResetTokenDTO(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}