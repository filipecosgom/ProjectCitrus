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
 * Provides data access methods specific to activation tokens, extending the basic
 * CRUD operations from {@link AbstractRepository}.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
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
     */
    public ActivationTokenRepository() {
        super(ActivationTokenEntity.class);
    }

    /**
     * Retrieves the temporary user entity associated with an activation token.
     *
     * @param activationTokenValue The activation token value.
     * @return The TemporaryUserEntity if found, null otherwise.
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
     *
     * @param activationTokenValue The activation token value.
     * @return The ActivationTokenEntity if found, null otherwise.
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