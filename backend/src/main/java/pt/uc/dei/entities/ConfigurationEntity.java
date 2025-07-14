package pt.uc.dei.entities;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing system configuration settings.
 * Stores various time limits and administrative details.
 */
@NamedQuery(name = "Configuration.getLatestConfiguration", query = "SELECT configuration FROM ConfigurationEntity configuration ORDER BY configuration.id DESC")
@Entity
/**
 * Table definition for configuration settings.
 *
 * Indexes:
 * <ul>
 *   <li><b>idx_admin_updates</b>: For efficient queries by admin user (keep only if such queries exist).</li>
 * </ul>
 */
@Table(name="config", indexes = {
    /**
     * For efficient queries by admin user (keep only if such queries exist).
     */
    @Index(name = "idx_admin_updates", columnList = "admin")
})
public class ConfigurationEntity implements Serializable {

    /**
     * The unique identifier for the configuration entry.
     * Generated automatically.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The allowed login time duration (in minutes).
     * Must always be set and can be updated.
     */
    @Column(name = "login_time", nullable = false, unique = false, updatable = true)
    private Integer loginTime;

    /**
     * The allowed verification time duration (in minutes).
     * Must always be set and can be updated.
     */
    @Column(name = "verification_time", nullable = false, unique = false, updatable = true)
    private Integer verificationTime;

    /**
     * The allowed password reset time duration (in minutes).
     * Must always be set and can be updated.
     */
    @Column(name = "password_reset_time", nullable = false, unique = false, updatable = true)
    private Integer passwordResetTime;

    /**
     * The date and time when this configuration was created.
     * Cannot be updated once set.
     */
    @Column(name = "creation_date", nullable = false, unique = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * The administrator responsible for setting this configuration.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "admin", nullable = false, updatable = false)
    private UserEntity admin;

    // Getters and Setters

    /**
     * Retrieves the unique identifier for the configuration entry.
     * @return the configuration ID.
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier for the configuration entry.
     * @param id the configuration ID to set.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Retrieves the allowed login time duration (in minutes).
     * @return the login time duration.
     */
    public Integer getLoginTime() { return loginTime; }

    /**
     * Sets the allowed login time duration (in minutes).
     * @param loginTime the login time duration to set.
     */
    public void setLoginTime(Integer loginTime) { this.loginTime = loginTime; }

    /**
     * Retrieves the allowed verification time duration (in minutes).
     * @return the verification time duration.
     */
    public Integer getVerificationTime() { return verificationTime; }

    /**
     * Sets the allowed verification time duration (in minutes).
     * @param verificationTime the verification time duration to set.
     */
    public void setVerificationTime(Integer verificationTime) { this.verificationTime = verificationTime; }

    /**
     * Retrieves the allowed password reset time duration (in minutes).
     * @return the password reset time duration.
     */
    public Integer getPasswordResetTime() { return passwordResetTime; }

    /**
     * Sets the allowed password reset time duration (in minutes).
     * @param passwordResetTime the password reset time duration to set.
     */
    public void setPasswordResetTime(Integer passwordResetTime) { this.passwordResetTime = passwordResetTime; }

    /**
     * Retrieves the date and time when this configuration was created.
     * @return the creation date and time.
     */
    public LocalDateTime getCreationDate() { return creationDate; }

    /**
     * Sets the date and time when this configuration was created.
     * @param creationDate the creation date and time to set.
     */
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    /**
     * Retrieves the administrator responsible for this configuration.
     * @return the admin user entity.
     */
    public UserEntity getAdmin() { return admin; }

    /**
     * Sets the administrator responsible for this configuration.
     * @param admin the admin user entity to set.
     */
    public void setAdmin(UserEntity admin) { this.admin = admin; }
}