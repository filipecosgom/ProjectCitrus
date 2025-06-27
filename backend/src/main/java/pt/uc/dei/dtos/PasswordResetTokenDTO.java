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

    /**
     * Retrieves the unique identifier for the password reset token.
     * @return the password reset token ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the password reset token.
     * @param id the password reset token ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the value of the password reset token.
     * @return the password reset token value.
     */
    public String getTokenValue() {
        return tokenValue;
    }

    /**
     * Sets the value of the password reset token.
     * @param tokenValue the password reset token value to set.
     */
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    /**
     * Retrieves the creation date and time of the password reset token.
     * @return the creation date and time.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date and time of the password reset token.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the user associated with this password reset token.
     * @return the user DTO.
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Sets the user associated with this password reset token.
     * @param user the user DTO to set.
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }
}