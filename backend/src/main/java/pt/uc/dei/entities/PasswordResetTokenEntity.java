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
@Entity
@Table(name = "passwordresettoken",
        indexes = @Index(name = "idx_passwordresettoken_tokenvalue",
                columnList = "tokenValue",
                unique = true))
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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }



}