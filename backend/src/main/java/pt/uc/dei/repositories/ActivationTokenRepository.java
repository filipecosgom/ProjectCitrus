package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.ActivationTokenDTO;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.PasswordResetTokenEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.UserEntity;

import java.util.List;

/**
 * Repository class for handling persistence operations for {@link ActivationTokenEntity}.
 * <p>
 * <b>Responsibilities:</b>
 * <ul>
 *   <li>Provides data access methods specific to activation tokens, extending the basic CRUD operations from {@link AbstractRepository}.</li>
 *   <li>Handles retrieval of activation tokens and their associated temporary users.</li>
 *   <li>Supports lookup of tokens by value and by user, with robust error handling and logging.</li>
 * </ul>
 * <p>
 * <b>EJB Details:</b> This class is marked as {@code @Stateless}, making it eligible for dependency injection
 * and transaction management by the EJB container.
 *
 * @author Project Citrus Team
 * @version 1.0
 */
@Stateless
public class ActivationTokenRepository extends AbstractRepository<ActivationTokenEntity> {
    /**
     * Logger instance for tracking activation token operations.
     */
    private static final Logger LOGGER = LogManager.getLogger(ActivationTokenRepository.class);

    /**
     * Serial version UID for ensuring class consistency during serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor initializing repository with ActivationTokenEntity type.
     * Calls the superclass constructor with the entity class type.
     */
    public ActivationTokenRepository() {
        super(ActivationTokenEntity.class);
    }

    /**
     * Retrieves the temporary user entity associated with an activation token value.
     * <p>
     * Executes the named query {@code ActivationToken.findTemporaryUserByToken} to find the user.
     * Logs a warning if no user is found, or an error if another exception occurs.
     *
     * @param activationTokenValue The activation token value to search for.
     * @return The {@link TemporaryUserEntity} if found, or {@code null} if not found or on error.
     */
    public TemporaryUserEntity getIdFromToken(String activationTokenValue) {
        try {
            return em.createNamedQuery("ActivationToken.findTemporaryUserByToken", TemporaryUserEntity.class)
                    .setParameter("tokenValue", activationTokenValue)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("No temporary user found for token: {}", activationTokenValue);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error retrieving temporary user ID for token: {}", activationTokenValue, e);
            return null;
        }
    }

    /**
     * Retrieves the activation token entity using the token value.
     * <p>
     * Executes the named query {@code ActivationToken.findActivationTokenByValue} to find the token entity.
     * Logs a warning if no token is found, or an error if another exception occurs.
     *
     * @param activationTokenValue The activation token value to search for.
     * @return The {@link ActivationTokenEntity} if found, or {@code null} if not found or on error.
     */
    public ActivationTokenEntity getTokenFromValue(String activationTokenValue) {
        try {
            return em.createNamedQuery("ActivationToken.findActivationTokenByValue", ActivationTokenEntity.class)
                    .setParameter("tokenValue", activationTokenValue)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("No token found for token: {}", activationTokenValue);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error retrieving token for token: {}", activationTokenValue, e);
            return null;
        }
    }

    /**
     * Retrieves all activation tokens associated with a given temporary user.
     * <p>
     * Executes the named query {@code ActivationToken.findActivationTokensOfUser} to find all tokens for the user.
     * Logs a warning if no tokens are found, or an error if another exception occurs.
     *
     * @param user The {@link TemporaryUserEntity} whose tokens are to be retrieved.
     * @return A list of {@link ActivationTokenEntity} for the user, or {@code null} if none found or on error.
     */
    public List<ActivationTokenEntity> getTokensOfUser(TemporaryUserEntity user) {
        try {
            return em.createNamedQuery("ActivationToken.findActivationTokensOfUser", ActivationTokenEntity.class)
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
}