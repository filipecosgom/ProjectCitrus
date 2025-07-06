package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.entities.CycleEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.CycleState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing {@link CycleEntity} persistence operations.
 * <p>
 * Provides data access methods specific to cycle entities, extending the basic
 * CRUD operations from {@link AbstractRepository}. This class handles all
 * database interactions related to cycle management.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for
 * dependency injection and transaction management by the EJB container.
 */
@Stateless
public class CycleRepository extends AbstractRepository<CycleEntity> {

    /**
     * Logger instance for logging operations within this class.
     */
    private static final Logger LOGGER = LogManager.getLogger(CycleRepository.class);

    /**
     * Serial version UID for serialization support.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CycleRepository instance.
     * Initializes the repository for {@link CycleEntity} operations.
     */
    public CycleRepository() {
        super(CycleEntity.class);
    }

    public List<CycleEntity> getAllCycles() {
        TypedQuery<CycleEntity> query = em.createQuery(
                "SELECT DISTINCT c FROM CycleEntity c " +
                "LEFT JOIN FETCH c.evaluations " +
                "LEFT JOIN FETCH c.evaluations.appraisedUser " +
                "ORDER BY c.startDate DESC",
                CycleEntity.class
        );
        return query.getResultList();
    }

    public long getTotalCycles() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CycleEntity> root = query.from(CycleEntity.class);
        query.select(cb.count(root));
        return em.createQuery(query).getSingleResult();
    }

    /**
     * Finds all cycles with a specific state.
     *
     * @param state The cycle state to filter by
     * @return List of cycles with the specified state
     */
    public List<CycleEntity> findCyclesByState(CycleState state) {
        try {
            TypedQuery<CycleEntity> query = em.createQuery(
                "SELECT c FROM CycleEntity c WHERE c.state = :state ORDER BY c.startDate DESC", 
                CycleEntity.class
            );
            query.setParameter("state", state);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding cycles by state: {}", state, e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds the current active cycle (OPEN state and current date within range).
     *
     * @return The current active cycle, or null if none exists
     */
    public CycleEntity findCurrentActiveCycle() {
        try {
            LocalDate now = LocalDate.now();
            TypedQuery<CycleEntity> query = em.createQuery(
                "SELECT c FROM CycleEntity c WHERE c.state = :state " +
                "AND c.startDate <= :now AND c.endDate >= :now " +
                "ORDER BY c.startDate DESC", 
                CycleEntity.class
            );
            query.setParameter("state", CycleState.OPEN);
            query.setParameter("now", now);
            query.setMaxResults(1);
            
            List<CycleEntity> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            LOGGER.error("Error finding current active cycle", e);
            return null;
        }
    }

    /**
     * Finds cycles managed by a specific administrator.
     *
     * @param adminId The ID of the administrator
     * @return List of cycles managed by the specified admin
     */
    public List<CycleEntity> findCyclesByAdmin(Long adminId) {
        try {
            TypedQuery<CycleEntity> query = em.createQuery(
                "SELECT c FROM CycleEntity c WHERE c.admin.id = :adminId ORDER BY c.startDate DESC", 
                CycleEntity.class
            );
            query.setParameter("adminId", adminId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding cycles by admin ID: {}", adminId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds cycles within a specific date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of cycles that overlap with the specified date range
     */
    public List<CycleEntity> findCyclesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            TypedQuery<CycleEntity> query = em.createQuery(
                "SELECT c FROM CycleEntity c WHERE " +
                "(c.startDate <= :endDate AND c.endDate >= :startDate) " +
                "ORDER BY c.startDate DESC", 
                CycleEntity.class
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding cycles by date range: {} to {}", startDate, endDate, e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds upcoming cycles (start date in the future).
     *
     * @return List of upcoming cycles
     */
    public List<CycleEntity> findUpcomingCycles() {
        try {
            LocalDate now = LocalDate.now();
            TypedQuery<CycleEntity> query = em.createQuery(
                "SELECT c FROM CycleEntity c WHERE c.startDate > :now ORDER BY c.startDate ASC", 
                CycleEntity.class
            );
            query.setParameter("now", now);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding upcoming cycles", e);
            return new ArrayList<>();
        }
    }

    /**
     * Finds expired cycles (end date in the past) that are still marked as OPEN.
     *
     * @return List of expired cycles that should be closed
     */
    public List<CycleEntity> findExpiredOpenCycles() {
        try {
            LocalDate now = LocalDate.now();
            TypedQuery<CycleEntity> query = em.createQuery(
                "SELECT c FROM CycleEntity c WHERE c.state = :state AND c.endDate < :now " +
                "ORDER BY c.endDate DESC", 
                CycleEntity.class
            );
            query.setParameter("state", CycleState.OPEN);
            query.setParameter("now", now);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding expired open cycles", e);
            return new ArrayList<>();
        }
    }

    /**
     * Checks if there are any overlapping cycles with the given date range.
     *
     * @param startDate The start date to check
     * @param endDate The end date to check
     * @param excludeCycleId Optional cycle ID to exclude from the check (for updates)
     * @return True if there are overlapping cycles, false otherwise
     */
    public boolean hasOverlappingCycles(LocalDate startDate, LocalDate endDate, Long excludeCycleId) {
        try {
            String jpql = "SELECT COUNT(c) FROM CycleEntity c WHERE " +
                         "(c.startDate <= :endDate AND c.endDate >= :startDate)";
            
            if (excludeCycleId != null) {
                jpql += " AND c.id != :excludeCycleId";
            }
            
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            if (excludeCycleId != null) {
                query.setParameter("excludeCycleId", excludeCycleId);
            }
            
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            LOGGER.error("Error checking for overlapping cycles", e);
            return false;
        }
    }

    /**
     * Finds cycles with advanced filtering options.
     *
     * @param state Optional filter by cycle state
     * @param adminId Optional filter by administrator ID
     * @param startDateFrom Optional filter for cycles starting after this date
     * @param startDateTo Optional filter for cycles starting before this date
     * @param limit Maximum number of results (optional)
     * @param offset Starting position for pagination (optional)
     * @return List of filtered cycles
     */
    public List<CycleEntity> findCyclesWithFilters(CycleState state, Long adminId, 
                                                  LocalDate startDateFrom, LocalDate startDateTo,
                                                  Integer limit, Integer offset) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CycleEntity> cq = cb.createQuery(CycleEntity.class);
            Root<CycleEntity> cycle = cq.from(CycleEntity.class);
            
            // CORREÇÃO: Usar Fetch sem aliases
            cycle.fetch("evaluations", JoinType.LEFT)
                 .fetch("appraisedUser", JoinType.LEFT);
            
            cq.select(cycle).distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            
            if (state != null) {
                predicates.add(cb.equal(cycle.get("state"), state));
            }
            
            if (adminId != null) {
                predicates.add(cb.equal(cycle.get("admin").get("id"), adminId));
            }
            
            if (startDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(cycle.get("startDate"), startDateFrom));
            }
            
            if (startDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(cycle.get("startDate"), startDateTo));
            }
            
            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            
            // Ordenar por startDate descendente (mais recente primeiro)
            cq.orderBy(cb.desc(cycle.get("startDate")));
            
            TypedQuery<CycleEntity> query = em.createQuery(cq);
            
            if (offset != null && offset > 0) {
                query.setFirstResult(offset);
            }
            
            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }
            
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding cycles with filters", e);
            return new ArrayList<>();
        }
    }

    public long countCyclesWithFilters(CycleState state, Long adminId,
                                       LocalDate startDateFrom, LocalDate startDateTo) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<CycleEntity> cycle = cq.from(CycleEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            if (state != null) {
                predicates.add(cb.equal(cycle.get("state"), state));
            }

            if (adminId != null) {
                predicates.add(cb.equal(cycle.get("admin").get("id"), adminId));
            }

            if (startDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(cycle.get("startDate"), startDateFrom));
            }

            if (startDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(cycle.get("startDate"), startDateTo));
            }

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            cq.select(cb.countDistinct(cycle)); // countDistinct avoids overcounting if joins are added later

            return em.createQuery(cq).getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error counting cycles with filters", e);
            return 0L;
        }
    }
}
