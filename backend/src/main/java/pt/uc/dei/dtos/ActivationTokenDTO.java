package pt.uc.dei.dtos;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing an activation token.
 * Stores token details associated with a temporary user.
 */
public class ActivationTokenDTO {

    /**
     * The unique identifier for the activation token.
     */
    private Long id;

    /**
     * The value of the activation token.
     * Used for user account activation.
     */
    private String tokenValue;

    /**
     * The creation date and time of the activation token.
     */
    private LocalDateTime creationDate;

    /**
     * The temporary user associated with this activation token.
     */
    private TemporaryUserDTO temporaryUser;

    // Getters and Setters

    /**
     * Retrieves the unique identifier for the activation token.
     * @return the activation token ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the activation token.
     * @param id the activation token ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the value of the activation token.
     * @return the activation token value.
     */
    public String getTokenValue() {
        return tokenValue;
    }

    /**
     * Sets the value of the activation token.
     * @param tokenValue the activation token value to set.
     */
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    /**
     * Retrieves the creation date and time of the activation token.
     * @return the creation date and time.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date and time of the activation token.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the temporary user associated with this activation token.
     * @return the temporary user.
     */
    public TemporaryUserDTO getTemporaryUser() {
        return temporaryUser;
    }

    /**
     * Sets the temporary user associated with this activation token.
     * @param temporaryUser the temporary user to set.
     */
    public void setTemporaryUser(TemporaryUserDTO temporaryUser) {
        this.temporaryUser = temporaryUser;
    }
}