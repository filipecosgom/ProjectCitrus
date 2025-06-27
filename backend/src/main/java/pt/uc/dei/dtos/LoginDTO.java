package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for user login requests.
 * Stores the credentials required for authentication.
 */
public class LoginDTO {

    /**
     * The email address of the user.
     * Must not be blank.
     */
    @NotNull(message = "Email is required!!")
    @NotBlank(message = "Email missing")
    private String email;

    /**
     * The password of the user.
     * Must not be blank.
     */
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password missing")
    private String password;

    @NotNull(message = "Authentication Code is required")
    private String authenticationCode;

    /**
     * Default constructor for `LoginDTO`.
     */
    public LoginDTO() {
    }

    /**
     * Retrieves the user's email.
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
     * @return The password.
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

    /**
     * Retrieves the authentication code for the user.
     *
     * @return The authentication code.
     */
    public String getAuthenticationCode() {
        return authenticationCode;
    }

    /**
     * Sets the authentication code for the user.
     *
     * @param authenticationCode The authentication code to be set.
     */
    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }
}