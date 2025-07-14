package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing system configuration.
 * Stores configuration settings for various time durations and creation information.
 */
public class ConfigurationDTO {

    /**
     * The unique identifier for the configuration entry.
     */
    private Long id;

    /**
     * The allowed login time duration (in minutes).
     * Must always be sent and must not be null.
     */
    @NotNull(message = "Login time missing")
    private Integer loginTime;

    /**
     * The allowed verification time duration (in minutes).
     * Must always be sent and must not be null.
     */
    @NotNull(message = "Verification time missing")
    private Integer verificationTime;

    /**
     * The allowed password reset time duration (in minutes).
     * Must always be sent and must not be null.
     */
    @NotNull(message = "Password reset time missing")
    private Integer passwordResetTime;

    private Boolean twoFactorAuthEnabled;

    /**
     * The date and time when this configuration was created.
     */
    private LocalDateTime creationDate;

    /**
     * The ID of the administrator responsible for setting this configuration.
     */
    private Long adminId;

    /**
     * The administrator details responsible for setting this configuration.
     */
    private UserDTO admin;

    /**
     * Default constructor for `ConfigurationDTO`.
     */
    public ConfigurationDTO() {
    }

    /**
     * Retrieves the unique identifier for the configuration entry.
     * @return the configuration ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the configuration entry.
     * @param id the configuration ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the allowed login time duration (in minutes).
     * @return the login time duration.
     */
    public Integer getLoginTime() {
        return loginTime;
    }

    /**
     * Sets the allowed login time duration (in minutes).
     * @param loginTime the login time duration to set.
     */
    public void setLoginTime(Integer loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * Retrieves the allowed verification time duration (in minutes).
     * @return the verification time duration.
     */
    public Integer getVerificationTime() {
        return verificationTime;
    }

    /**
     * Sets the allowed verification time duration (in minutes).
     * @param verificationTime the verification time duration to set.
     */
    public void setVerificationTime(Integer verificationTime) {
        this.verificationTime = verificationTime;
    }

    /**
     * Retrieves the allowed password reset time duration (in minutes).
     * @return the password reset time duration.
     */
    public Integer getPasswordResetTime() {
        return passwordResetTime;
    }

    /**
     * Sets the allowed password reset time duration (in minutes).
     * @param passwordResetTime the password reset time duration to set.
     */
    public void setPasswordResetTime(Integer passwordResetTime) {
        this.passwordResetTime = passwordResetTime;
    }

    /**
     * Retrieves the date and time when this configuration was created.
     * @return the creation date and time.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the date and time when this configuration was created.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the ID of the administrator responsible for this configuration.
     * @return the admin ID.
     */
    public Long getAdminId() {
        return adminId;
    }

    /**
     * Sets the ID of the administrator responsible for this configuration.
     * @param adminId the admin ID to set.
     */
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    /**
     * Retrieves the administrator details responsible for this configuration.
     * @return the admin user DTO.
     */
    public UserDTO getAdmin() {
        return admin;
    }

    /**
     * Sets the administrator details responsible for this configuration.
     * @param admin the admin user DTO to set.
     */
    public void setAdmin(UserDTO admin) {
        this.admin = admin;
    }

    public Boolean getTwoFactorAuthEnabled() {
        return twoFactorAuthEnabled;
    }

    public void setTwoFactorAuthEnabled(Boolean twoFactorAuthEnabled) {
        this.twoFactorAuthEnabled = twoFactorAuthEnabled;
    }
}