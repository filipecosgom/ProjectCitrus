package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.CycleState;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing {@link AppraisalEntity} persistence operations.
 * <p>
 * Provides data access methods specific to appraisal entities, extending the basic
 * CRUD operations from {@link AbstractRepository}. This class handles all
 * database interactions related to appraisal management.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for
 * dependency injection and transaction management by the EJB container.
 */
@Stateless
public class AppraisalRepository extends AbstractRepository<AppraisalEntity> {

    /**
     * Logger instance for logging operations within this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(AppraisalRepository.class);

    /**
     * Serial version UID for serialization support.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AppraisalRepository instance.
     * Initializes the repository for {@link AppraisalEntity} operations.
     */
    public AppraisalRepository() {
        super(AppraisalEntity.class);
    }

    /**
     * Finds all appraisals for a specific appraised user.
     *
     * @param userId The ID of the user being appraised
     * @return List of appraisals for the specified user
     */
    public List<AppraisalEntity> findAppraisalsByAppraisedUser(Long userId) {
        try {
            TypedQuery<AppraisalEntity> query = em.createQuery(
                "SELECT a FROM AppraisalEntity a WHERE a.appraisedUser.id = :userId ORDER BY a.creationDate DESC", 
                AppraisalEntity.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding appraisals by appraised user ID: {}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds all appraisals created by a specific appraising user (manager).
     *
     * @param managerId The ID of the user performing the appraisals
     * @return List of appraisals created by the specified manager
     */
    public List<AppraisalEntity> findAppraisalsByAppraisingUser(Long managerId) {
        try {
            TypedQuery<AppraisalEntity> query = em.createQuery(
                "SELECT a FROM AppraisalEntity a WHERE a.appraisingUser.id = :managerId ORDER BY a.creationDate DESC", 
                AppraisalEntity.class
            );
            query.setParameter("managerId", managerId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding appraisals by appraising user ID: {}", managerId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds all appraisals within a specific cycle.
     *
     * @param cycleId The ID of the cycle
     * @return List of appraisals within the specified cycle
     */
    public List<AppraisalEntity> findAppraisalsByCycle(Long cycleId) {
        try {
            TypedQuery<AppraisalEntity> query = em.createQuery(
                "SELECT a FROM AppraisalEntity a WHERE a.cycle.id = :cycleId ORDER BY a.creationDate DESC", 
                AppraisalEntity.class
            );
            query.setParameter("cycleId", cycleId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding appraisals by cycle ID: {}", cycleId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds all appraisals with a specific state.
     *
     * @param state The appraisal state to filter by
     * @return List of appraisals with the specified state
     */
    public List<AppraisalEntity> findAppraisalsByState(AppraisalState state) {
        try {
            TypedQuery<AppraisalEntity> query = em.createQuery(
                "SELECT a FROM AppraisalEntity a WHERE a.state = :state ORDER BY a.creationDate DESC", 
                AppraisalEntity.class
            );
            query.setParameter("state", state);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding appraisals by state: {}", state, e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds an appraisal by appraised user, appraising user, and cycle.
     * Useful to check if an appraisal already exists for a specific combination.
     *
     * @param appraisedUserId The ID of the user being appraised
     * @param appraisingUserId The ID of the user performing the appraisal
     * @param cycleId The ID of the cycle
     * @return The appraisal entity if found, null otherwise
     */
    public AppraisalEntity findAppraisalByUsersAndCycle(Long appraisedUserId, Long appraisingUserId, Long cycleId) {
        try {
            TypedQuery<AppraisalEntity> query = em.createQuery(
                "SELECT a FROM AppraisalEntity a WHERE a.appraisedUser.id = :appraisedUserId " +
                "AND a.appraisingUser.id = :appraisingUserId AND a.cycle.id = :cycleId", 
                AppraisalEntity.class
            );
            query.setParameter("appraisedUserId", appraisedUserId);
            query.setParameter("appraisingUserId", appraisingUserId);
            query.setParameter("cycleId", cycleId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No appraisal found for users {} -> {} in cycle {}", appraisingUserId, appraisedUserId, cycleId);
            return null;
        } catch (Exception e) {
            LOGGER.error("Error finding appraisal by users and cycle", e);
            return null;
        }
    }

    /**
     * Finds appraisals with advanced filtering options.
     *
     * @param appraisedUserId Optional filter by appraised user ID
     * @param appraisingUserId Optional filter by appraising user ID
     * @param cycleId Optional filter by cycle ID
     * @param state Optional filter by appraisal state
     * @param limit Maximum number of results (optional)
     * @param offset Starting position for pagination (optional)
     * @return List of filtered appraisals
     */
    public List<AppraisalEntity> findAppraisalsWithFilters(Long appraisedUserId, Long appraisingUserId, 
                                                          Long cycleId, AppraisalState state, 
                                                          Integer limit, Integer offset) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AppraisalEntity> cq = cb.createQuery(AppraisalEntity.class);
            Root<AppraisalEntity> appraisal = cq.from(AppraisalEntity.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if (appraisedUserId != null) {
                predicates.add(cb.equal(appraisal.get("appraisedUser").get("id"), appraisedUserId));
            }
            
            if (appraisingUserId != null) {
                predicates.add(cb.equal(appraisal.get("appraisingUser").get("id"), appraisingUserId));
            }
            
            if (cycleId != null) {
                predicates.add(cb.equal(appraisal.get("cycle").get("id"), cycleId));
            }
            
            if (state != null) {
                predicates.add(cb.equal(appraisal.get("state"), state));
            }
            
            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            
            cq.orderBy(cb.desc(appraisal.get("creationDate")));
            
            TypedQuery<AppraisalEntity> query = em.createQuery(cq);
            
            if (offset != null && offset > 0) {
                query.setFirstResult(offset);
            }
            
            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }
            
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding appraisals with filters", e);
            return new ArrayList<>();
        }
    }

    /**
     * Counts total appraisals for a specific user (either as appraised or appraising).
     *
     * @param userId The user ID
     * @param asAppraised Whether to count as appraised user (true) or appraising user (false)
     * @return Total count of appraisals
     */
    public Long countAppraisalsByUser(Long userId, boolean asAppraised) {
        try {
            String jpql = asAppraised ? 
                "SELECT COUNT(a) FROM AppraisalEntity a WHERE a.appraisedUser.id = :userId" :
                "SELECT COUNT(a) FROM AppraisalEntity a WHERE a.appraisingUser.id = :userId";
                
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error counting appraisals for user ID: {}", userId, e);
            return 0L;
        }
    }

    /**
     * Finds appraisals by specific IDs.
     *
     * @param appraisalIds List of appraisal IDs
     * @return List of appraisal entities
     */
    public List<AppraisalEntity> findAppraisalsByIds(List<Long> appraisalIds) {
        if (appraisalIds == null || appraisalIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        TypedQuery<AppraisalEntity> query = em.createQuery(
            "SELECT a FROM AppraisalEntity a WHERE a.id IN :ids", 
            AppraisalEntity.class
        );
        query.setParameter("ids", appraisalIds);
        return query.getResultList();
    }

    /**
     * Finds all COMPLETED appraisals in an OPEN cycle.
     *
     * @param cycleId The cycle ID
     * @return List of completed appraisal entities
     */
    public List<AppraisalEntity> findCompletedAppraisalsByCycleId(Long cycleId) {
        TypedQuery<AppraisalEntity> query = em.createQuery(
            "SELECT a FROM AppraisalEntity a WHERE a.cycle.id = :cycleId " +
            "AND a.state = :state AND a.cycle.state = :cycleState", 
            AppraisalEntity.class
        );
        query.setParameter("cycleId", cycleId);
        query.setParameter("state", AppraisalState.COMPLETED);
        query.setParameter("cycleState", CycleState.OPEN);
        return query.getResultList();
    }

    /**
     * Finds all COMPLETED appraisals for a specific user in OPEN cycles.
     *
     * @param userId The user ID (appraised user)
     * @return List of completed appraisal entities
     */
    public List<AppraisalEntity> findCompletedAppraisalsByUserId(Long userId) {
        TypedQuery<AppraisalEntity> query = em.createQuery(
            "SELECT a FROM AppraisalEntity a WHERE a.appraisedUser.id = :userId " +
            "AND a.state = :state AND a.cycle.state = :cycleState", 
            AppraisalEntity.class
        );
        query.setParameter("userId", userId);
        query.setParameter("state", AppraisalState.COMPLETED);
        query.setParameter("cycleState", CycleState.OPEN);
        return query.getResultList();
    }

    /**
     * Finds all COMPLETED appraisals in all OPEN cycles.
     *
     * @return List of completed appraisal entities
     */
    public List<AppraisalEntity> findAllCompletedAppraisalsInOpenCycles() {
        TypedQuery<AppraisalEntity> query = em.createQuery(
            "SELECT a FROM AppraisalEntity a WHERE a.state = :state " +
            "AND a.cycle.state = :cycleState", 
            AppraisalEntity.class
        );
        query.setParameter("state", AppraisalState.COMPLETED);
        query.setParameter("cycleState", CycleState.OPEN);
        return query.getResultList();
    }

    /**
     * Validates that all specified appraisals are COMPLETED and in OPEN cycles.
     *
     * @param appraisalIds List of appraisal IDs to validate
     * @return List of valid appraisal entities
     */
    public List<AppraisalEntity> findValidAppraisalsForClosing(List<Long> appraisalIds) {
        if (appraisalIds == null || appraisalIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        TypedQuery<AppraisalEntity> query = em.createQuery(
            "SELECT a FROM AppraisalEntity a WHERE a.id IN :ids " +
            "AND a.state = :state AND a.cycle.state = :cycleState", 
            AppraisalEntity.class
        );
        query.setParameter("ids", appraisalIds);
        query.setParameter("state", AppraisalState.COMPLETED);
        query.setParameter("cycleState", CycleState.OPEN);
        return query.getResultList();
    }
}
