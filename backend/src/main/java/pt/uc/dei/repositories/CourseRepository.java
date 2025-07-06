package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing {@link CourseEntity} persistence operations.
 * <p>
 * Provides data access methods specific to course entities, extending the basic
 * CRUD operations from {@link AbstractRepository}. This class handles all
 * database interactions related to course management.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for
 * dependency injection and transaction management by the EJB container.
 */
@Stateless
public class CourseRepository extends AbstractRepository<CourseEntity> {

    private static final Logger LOGGER = LogManager.getLogger(CourseRepository.class);
    private static final long serialVersionUID = 1L;

    public CourseRepository() {
        super(CourseEntity.class);
    }

    public List<CourseEntity> getAllCourses() {
        TypedQuery<CourseEntity> query = em.createQuery(
                "SELECT c FROM CourseEntity c ORDER BY c.creationDate DESC",
                CourseEntity.class
        );
        return query.getResultList();
    }

    public long getTotalCourses() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CourseEntity> root = query.from(CourseEntity.class);
        query.select(cb.count(root));
        return em.createQuery(query).getSingleResult();
    }

    public List<CourseEntity> findCoursesByArea(CourseArea area) {
        try {
            TypedQuery<CourseEntity> query = em.createQuery(
                "SELECT c FROM CourseEntity c WHERE c.area = :area ORDER BY c.creationDate DESC",
                CourseEntity.class
            );
            query.setParameter("area", area);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding courses by area: {}", area, e);
            return new ArrayList<>();
        }
    }

    public List<CourseEntity> findCoursesByAdmin(Long adminId) {
        try {
            TypedQuery<CourseEntity> query = em.createQuery(
                "SELECT c FROM CourseEntity c WHERE c.admin.id = :adminId ORDER BY c.creationDate DESC",
                CourseEntity.class
            );
            query.setParameter("adminId", adminId);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding courses by admin ID: {}", adminId, e);
            return new ArrayList<>();
        }
    }

    public List<CourseEntity> findCoursesWithFilters(CourseArea area, Language language, Long adminId, Boolean isActive, Integer limit, Integer offset) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CourseEntity> cq = cb.createQuery(CourseEntity.class);
            Root<CourseEntity> course = cq.from(CourseEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            if (area != null) {
                predicates.add(cb.equal(course.get("area"), area));
            }
            if (language != null) {
                predicates.add(cb.equal(course.get("language"), language));
            }
            if (adminId != null) {
                predicates.add(cb.equal(course.get("admin").get("id"), adminId));
            }
            if (isActive != null) {
                predicates.add(cb.equal(course.get("courseIsActive"), isActive));
            }

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            cq.orderBy(cb.desc(course.get("creationDate")));

            TypedQuery<CourseEntity> query = em.createQuery(cq);
            if (offset != null && offset > 0) {
                query.setFirstResult(offset);
            }
            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding courses with filters", e);
            return new ArrayList<>();
        }
    }

    public long countCoursesWithFilters(CourseArea area, Language language, Long adminId, Boolean isActive) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<CourseEntity> course = cq.from(CourseEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            if (area != null) {
                predicates.add(cb.equal(course.get("area"), area));
            }
            if (language != null) {
                predicates.add(cb.equal(course.get("language"), language));
            }
            if (adminId != null) {
                predicates.add(cb.equal(course.get("admin").get("id"), adminId));
            }
            if (isActive != null) {
                predicates.add(cb.equal(course.get("courseIsActive"), isActive));
            }

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            cq.select(cb.countDistinct(course));
            return em.createQuery(cq).getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error counting courses with filters", e);
            return 0L;
        }
    }
}
