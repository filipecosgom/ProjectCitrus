package pt.uc.dei.repositories;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract base repository class providing common CRUD operations for JPA entities.
 * <p>
 * This class serves as a foundation for entity-specific repositories, implementing
 * standard data access patterns with transaction management.
 *
 * @param <T> The entity type this repository manages, must implement Serializable
 */
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public abstract class AbstractRepository<T extends Serializable> implements Serializable {
    private static final Logger logger = LogManager.getLogger(AbstractRepository.class);

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** The entity class this repository manages */
    private final Class<T> clazz;

    /** JPA EntityManager instance, injected by the container */
    @PersistenceContext(unitName = "projectcitrus")
    protected EntityManager em;

    /**
     * Constructs a new repository for the specified entity class.
     *
     * @param clazz The entity class this repository will manage
     */
    public AbstractRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Finds an entity by its primary key.
     *
     * @param id The primary key of the entity to find
     * @return The found entity instance or null if the entity does not exist
     */
    public T find(Object id) {
        return em.find(clazz, id);
    }

    /**
     * Persists a new entity in the database.
     *
     * @param entity The entity instance to persist
     * @throws jakarta.persistence.PersistenceException If a persistence error occurs
     */
    public void persist(final T entity) {
        em.persist(entity);
    }

    /**
     * Merges the state of the given entity into the current persistence context.
     *
     * @param entity The entity to merge
     * @return The managed instance that the state was merged to
     */
    public void merge(final T entity) {
        em.merge(entity);
    }

    /**
     * Removes an entity from the database.
     *
     * @param entity The entity to remove
     */
    public void remove(final T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    /**
     * Retrieves all entities of the managed type.
     *
     * @return A list of all entities
     */
    public List<T> findAll() {
        final CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clazz);
        criteriaQuery.select(criteriaQuery.from(clazz));
        return em.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Deletes all entities of the managed type.
     *
     * @return The number of entities deleted
     */
    public void deleteAll() {
        final CriteriaDelete<T> criteriaDelete = em.getCriteriaBuilder().createCriteriaDelete(clazz);
        criteriaDelete.from(clazz);
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Synchronizes the persistence context to the underlying database.
     */
    public void flush() {
        em.flush();
    }
}