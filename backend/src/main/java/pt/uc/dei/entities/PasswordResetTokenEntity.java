package pt.uc.dei.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a password reset token.
 * Used for securely verifying password reset requests.
 */
@NamedQueries({
        @NamedQuery(
                name = "PasswordResetToken.findPasswordResetTokensOfUser",
                query = "SELECT t FROM PasswordResetTokenEntity t WHERE t.user.id = :id"
        ),
        @NamedQuery(
                name = "PasswordResetToken.findPasswordResetTokenByValue",
                query = "SELECT t FROM PasswordResetTokenEntity t WHERE t.tokenValue = :tokenValue"
        )
})

/**
 * Entity representing a password reset token.
 * <p>
 * Indexes are added to optimize queries for token lookup, user-based filtering, and ordering by creation date.
 * <ul>
 *   <li>tokenValue: For fast lookup by token value (unique).</li>
 *   <li>user_id: For queries fetching all tokens for a user.</li>
 *   <li>user_id, creation_date: For ordering or filtering tokens by creation date for a user.</li>
 * </ul>
 */
@Entity
@Table(name = "passwordresettoken",
    indexes = {
        /**
         * Index for fast lookup by token value (unique).
         */
        @Index(name = "idx_passwordresettoken_tokenvalue", columnList = "tokenValue", unique = true),
        /**
         * Index for queries fetching all tokens for a user.
         */
        @Index(name = "idx_passwordresettoken_user_id", columnList = "user_id"),
        /**
         * Index for ordering or filtering tokens by creation date for a user.
         */
        @Index(name = "idx_passwordresettoken_user_creation_date", columnList = "user_id, creation_date")
    })
public class PasswordResetTokenEntity implements Serializable {

    /**
     * The unique identifier for the password reset token.
     * Generated automatically.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The value of the password reset token.
     * Must be unique and cannot be null.
     */
    @Column(name = "tokenValue", unique = true, nullable = false)
    private String tokenValue;

    /**
     * The date and time when the token was created.
     * Cannot be updated after initialization.
     */
    @Column(name = "creation_date", nullable = false, unique = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * The user associated with this password reset token.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity user;

    // Getters and Setters

    /**
     * Gets the unique identifier for the password reset token.
     * @return the token ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the password reset token.
     * @param id the token ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the value of the password reset token.
     * @return the token value
     */
    public String getTokenValue() {
        return tokenValue;
    }

    /**
     * Sets the value of the password reset token.
     * @param tokenValue the token value
     */
    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    /**
     * Gets the creation date and time of the token.
     * @return the creation date and time
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date and time of the token.
     * @param creationDate the creation date and time
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the user associated with this password reset token.
     * @return the user entity
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets the user associated with this password reset token.
     * @param user the user entity
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }



}