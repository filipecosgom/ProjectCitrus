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


@Stateless
public class PasswordResetTokenRepository extends AbstractRepository<PasswordResetTokenEntity> {

    private static final Logger LOGGER = LogManager.getLogger(PasswordResetTokenRepository.class);

    /**
     * Serial version UID for ensuring class consistency during serialization.
     */
    private static final long serialVersionUID = 1L;

    public PasswordResetTokenRepository() {
        super(PasswordResetTokenEntity.class);
    }

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

}