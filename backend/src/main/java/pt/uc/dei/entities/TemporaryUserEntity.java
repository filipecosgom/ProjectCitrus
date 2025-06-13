package pt.uc.dei.entities;

import jakarta.persistence.*;
import java.io.Serializable;
/**
 * Represents a temporary user account during registration process.
 * <p>
 * Stores unverified user credentials until email activation is completed.
 * Automatically cleaned up after successful activation or expiration.
 */
@NamedQuery(
        name = "TemporaryUser.findTemporaryUserByEmail",
        query = "SELECT u FROM TemporaryUserEntity u WHERE u.email = :email"
)
@Entity
@Table(
        name = "tempuseraccount",
        indexes = {
                @Index(name = "idx_tempuser_email", columnList = "email", unique = true)
        }
)
public class TemporaryUserEntity implements Serializable {

    /**
     * Unique database identifier for the temporary account.
     * Auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * User's email address for account verification.
     * <p>
     * Maximum length of 254 characters (RFC 5321 compliant).
     * Marked as unique to prevent duplicate registrations.
     */
    @Column(name = "email", nullable = false, unique = true, updatable = false, length = 254)
    private String email;

    /**
     * Encrypted password for the temporary account.
     * <p>
     * Should be hashed using secure algorithm before storage.
     */
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "two_factor_secret", nullable = false, updatable = false)
    private String twoFactorSecret;

    /**
     * Activation token associated with this temporary account.
     * <p>
     * Features:
     * - Bi-directional one-to-one mapping
     * - Automatic cascade operations
     * - Orphan removal when temporary user is deleted
     */
    @OneToOne(
            mappedBy = "temporaryUser",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ActivationTokenEntity activationToken;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ActivationTokenEntity getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(ActivationTokenEntity activationToken) {
        this.activationToken = activationToken;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }
}