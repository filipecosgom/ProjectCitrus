package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.CourseParameter;
import pt.uc.dei.enums.Language;
import pt.uc.dei.enums.OrderBy;
import pt.uc.dei.utils.SearchUtils;

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

    public List<CourseEntity> findCoursesWithFilters(
            Long id, String title, Integer duration, String description,
            CourseArea area, Language language, String adminName, Boolean courseIsActive,
            CourseParameter parameter, OrderBy orderBy,
            Integer offset, Integer limit) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<CourseEntity> cq = cb.createQuery(CourseEntity.class);
            Root<CourseEntity> course = cq.from(CourseEntity.class);
            Join<CourseEntity, UserEntity> adminJoin = course.join("admin", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();

            // ...existing predicate logic, but use cb.function(\"unaccent\", ...) if needed...

            cq.where(predicates.toArray(new Predicate[0]));

            // Sorting
            if (parameter != null) {
                Path<?> sortingField;
                if ("admin.name".equals(parameter.getFieldName())) {
                    sortingField = adminJoin.get("name");
                } else {
                    sortingField = course.get(parameter.getFieldName());
                }
                cq.orderBy(orderBy == OrderBy.DESCENDING ? cb.desc(sortingField) : cb.asc(sortingField));
            } else {
                cq.orderBy(cb.desc(course.get("creationDate")));
            }

            TypedQuery<CourseEntity> typedQuery = em.createQuery(cq);
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(limit);
            return typedQuery.getResultList();
        } catch (Exception e) {
            LOGGER.error("Error finding courses with filters", e);
            return new ArrayList<>();
        }
    }

    public long countCoursesWithFilters(Long id, String title, Integer duration, String description,
                                        CourseArea area, Language language, String adminName, Boolean courseIsActive,
                                        CourseParameter parameter, OrderBy orderBy) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<CourseEntity> course = cq.from(CourseEntity.class);
            Join<CourseEntity, UserEntity> adminJoin = course.join("admin", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();

            // ...same predicate logic as above...

            cq.where(predicates.toArray(new Predicate[0]));
            cq.select(cb.count(course));
            return em.createQuery(cq).getSingleResult();
        } catch (Exception e) {
            LOGGER.error("Error counting courses with filters", e);
            return 0L;
        }
    }

    // Update: find by id (now Long id is PK)
    public CourseEntity findCourseById(Long id) {
        try {
            return em.find(CourseEntity.class, id);
        } catch (Exception e) {
            LOGGER.error("Error finding course by id: {}", id, e);
            return null;
        }
    }
}
