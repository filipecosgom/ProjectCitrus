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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Integer loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getVerificationTime() {
        return verificationTime;
    }

    public void setVerificationTime(Integer verificationTime) {
        this.verificationTime = verificationTime;
    }

    public Integer getPasswordResetTime() {
        return passwordResetTime;
    }

    public void setPasswordResetTime(Integer passwordResetTime) {
        this.passwordResetTime = passwordResetTime;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public UserDTO getAdmin() {
        return admin;
    }

    public void setAdmin(UserDTO admin) {
        this.admin = admin;
    }
}