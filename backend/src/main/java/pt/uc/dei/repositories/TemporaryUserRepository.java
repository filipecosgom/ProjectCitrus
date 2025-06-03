package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.UserEntity;

/**
 * Repository class for handling persistence operations for {@link TemporaryUserEntity}.
 * <p>
 * Provides data access methods specific to temporary user entities, extending the basic
 * CRUD operations from {@link AbstractRepository}.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
 */
@Stateless
public class TemporaryUserRepository extends AbstractRepository<TemporaryUserEntity> {
    private static final Logger LOGGER = LogManager.getLogger(TemporaryUserRepository.class);

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new TemporaryUserRepository instance.
     * Initializes the repository for {@link TemporaryUserEntity} operations.
     */
    public TemporaryUserRepository() {
        super(TemporaryUserEntity.class);
    }

    /**
     * Finds a temporary user by their email address.
     *
     * @param email The email address to search for
     * @return The {@link TemporaryUserEntity} matching the email, or null if not found
     * @throws jakarta.persistence.PersistenceException If a persistence error occurs
     *
     * @implNote Uses a named query "TemporaryUser.findTemporaryUserByEmail" to perform the lookup.
     * Logs a warning if no user is found with the specified email.
     */
    public TemporaryUserEntity findTemporaryUserByEmail(String email) {
        try {
            return em.createNamedQuery("TemporaryUser.findTemporaryUserByEmail", TemporaryUserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("Temporary user not found with email: " + email);
            return null;
        }
    }
}