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

    public TemporaryUserDTO getTemporaryUser() {
        return temporaryUser;
    }

    public void setTemporaryUser(TemporaryUserDTO temporaryUser) {
        this.temporaryUser = temporaryUser;
    }
}