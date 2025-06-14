package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) representing a temporary user.
 * Used for user registration and authentication processes.
 */
public class TemporaryUserDTO {

    /**
     * The unique identifier for the user.
     */
    private Long id;

    /**
     * The email address of the user.
     * Must always be sent and must not be blank.
     */
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /**
     * The password of the user.
     * Must always be sent and must not be blank.
     */
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be empty")
    private String password;

    private String secretKey;
    // Getters and Setters

    /**
     * Retrieves the user ID.
     *
     * @return The user ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id The user ID to be set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the user's email address.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The email address to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the user's password.
     *
     * @return The hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The password to be set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}