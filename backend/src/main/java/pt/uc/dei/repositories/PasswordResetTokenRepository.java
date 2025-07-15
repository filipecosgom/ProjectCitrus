package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.PasswordResetTokenEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.UserEntity;

import java.util.List;



/**
 * Repository class for managing PasswordResetTokenEntity persistence operations.
 * Provides methods to retrieve password reset tokens by user or by token value.
 * Extends the generic AbstractRepository for common CRUD operations.
 */
@Stateless
public class PasswordResetTokenRepository extends AbstractRepository<PasswordResetTokenEntity> {

    private static final Logger LOGGER = LogManager.getLogger(PasswordResetTokenRepository.class);

    /**
     * Serial version UID for ensuring class consistency during serialization.
     */
    private static final long serialVersionUID = 1L;


    /**
     * Constructs a new PasswordResetTokenRepository and sets the entity class to PasswordResetTokenEntity.
     */
    public PasswordResetTokenRepository() {
        super(PasswordResetTokenEntity.class);
    }


    /**
     * Retrieves all password reset tokens associated with a given user.
     *
     * @param user The user whose password reset tokens are to be retrieved.
     * @return A list of PasswordResetTokenEntity objects for the user, or null if none found or an error occurs.
     */
    public List<PasswordResetTokenEntity> getTokensOfUser(UserEntity user) {
        try {
            return em.createNamedQuery("PasswordResetToken.findPasswordResetTokensOfUser", PasswordResetTokenEntity.class)
                    .setParameter("id", user.getId())
                    .getResultList();
        } catch (NoResultException e) {
            LOGGER.warn("No tokens found for user: {}", user.getEmail());
            return null;
        } catch (Exception e) {
            LOGGER.error("Error retrieving tokens for user: {}", user.getEmail(), e);
            return null;
        }
    }


    /**
     * Retrieves a password reset token entity by its token value.
     *
     * @param passwordResetTokenValue The value of the password reset token.
     * @return The PasswordResetTokenEntity if found, or null if not found or an error occurs.
     */
    public PasswordResetTokenEntity getTokenFromValue(String passwordResetTokenValue) {
        try {
            return em.createNamedQuery("PasswordResetToken.findPasswordResetTokenByValue", PasswordResetTokenEntity.class)
                    .setParameter("tokenValue", passwordResetTokenValue)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("No token found for token: {}", passwordResetTokenValue);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error retrieving token for token: {}", passwordResetTokenValue, e);
            return null;
        }
    }





}