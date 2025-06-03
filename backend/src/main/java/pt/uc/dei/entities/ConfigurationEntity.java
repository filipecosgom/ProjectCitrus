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
@Table(name="config", indexes = {
        @Index(name = "idx_latest_configuration", columnList = "id DESC"),
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

    public UserEntity getAdmin() {
        return admin;
    }

    public void setAdmin(UserEntity admin) {
        this.admin = admin;
    }
}