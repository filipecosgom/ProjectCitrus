package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.UserEntity;

/**
 * Repository class for managing {@link UserEntity} persistence operations.
 * <p>
 * Provides data access methods specific to user entities, extending the basic
 * CRUD operations from {@link AbstractRepository}. This class handles all
 * database interactions related to user management.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for
 *            dependency injection and transaction management by the EJB container.
 */
@Stateless
public class UserRepository extends AbstractRepository<UserEntity> {

    /**
     * Logger instance for logging operations within this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserRepository.class);

    /**
     * Serial version UID for serialization support.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UserRepository instance.
     * Initializes the repository for {@link UserEntity} operations.
     */
    public UserRepository() {
        super(UserEntity.class);
    }

    /**
     * Retrieves a user entity by email address.
     * <p>
     * This method executes a named query "User.findUserByEmail" to locate
     * a user with the specified email address. If no user is found,
     * the method logs a warning and returns null.
     *
     * @param email The email address to search for (case-sensitive)
     * @return The {@link UserEntity} matching the email address, or
     *         null if no user is found
     * @throws jakarta.persistence.PersistenceException If an error occurs
     *         during the database operation
     * @throws jakarta.persistence.NonUniqueResultException If multiple users
     *         are found with the same email (should not occur if email is unique)
     *
     * @see UserEntity
     */
    public UserEntity findUserByEmail(String email) {
        try {
            return em.createNamedQuery("User.findUserByEmail", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("User not found with email: " + email);
            return null;
        }
    }

    /**
     * Retrieves a user entity by its unique identifier.
     * <p>
     * This method executes a named query "User.findUserById" to locate
     * a user with the specified ID. If no user is found, the method
     * logs a warning and returns null.
     *
     * @param id The unique identifier to search for
     * @return The {@link UserEntity} matching the ID, or
     *         null if no user is found
     * @throws jakarta.persistence.PersistenceException If an error occurs
     *         during the database operation
     * @throws jakarta.persistence.NonUniqueResultException If multiple users
     *         are found with the same ID (should not occur as ID is unique)
     *
     * @see UserEntity
     */
    public UserEntity findUserById(Long id) {
        try {
            return em.createNamedQuery("User.findUserById", UserEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("User not found with ID: " + id);
            return null;
        }
    }
}