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

    @Column(name = "secretKey", nullable = false, updatable = false)
    private String secretKey;

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

    /**
     * Gets the unique identifier for the temporary user account.
     * @return the account ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the temporary user account.
     * @param id the account ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the email address for account verification.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address for account verification.
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the encrypted password for the temporary account.
     * @return the encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the encrypted password for the temporary account.
     * @param password the encrypted password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the secret key for the temporary account.
     * @return the secret key
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Sets the secret key for the temporary account.
     * @param secretKey the secret key
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Gets the activation token associated with this temporary account.
     * @return the activation token entity
     */
    public ActivationTokenEntity getActivationToken() {
        return activationToken;
    }

    /**
     * Sets the activation token associated with this temporary account.
     * @param activationToken the activation token entity
     */
    public void setActivationToken(ActivationTokenEntity activationToken) {
        this.activationToken = activationToken;
    }
}