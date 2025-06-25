package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.*;
import pt.uc.dei.enums.Order;
import pt.uc.dei.utils.SearchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing {@link UserEntity} persistence operations.
 * <p>
 * Provides data access methods specific to user entities, extending the basic
 * CRUD operations from {@link AbstractRepository}. This class handles all
 * database interactions related to user management.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for
 * dependency injection and transaction management by the EJB container.
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
     * null if no user is found
     * @throws jakarta.persistence.PersistenceException     If an error occurs
     *                                                      during the database operation
     * @throws jakarta.persistence.NonUniqueResultException If multiple users
     *                                                      are found with the same email (should not occur if email is unique)
     * @see UserEntity
     */
    public UserEntity findUserByEmail(String email) {
        try {
            return em.createNamedQuery("User.findUserByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
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
     * null if no user is found
     * @throws jakarta.persistence.PersistenceException     If an error occurs
     *                                                      during the database operation
     * @throws jakarta.persistence.NonUniqueResultException If multiple users
     *                                                      are found with the same ID (should not occur as ID is unique)
     * @see UserEntity
     */
    public UserEntity findUserById(Long id) {
        try {
            return em.createNamedQuery("User.findUserById", UserEntity.class).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warn("User not found with ID: " + id);
            return null;
        }
    }

    public List<UserEntity> getUsers(Long id, String email, String name, String phone, AccountState accountState, String roleStr, Office office, Parameter parameter, Order order, int offset, int limit) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);
        root.fetch("managerUser", JoinType.LEFT);


        List<Predicate> predicates = new ArrayList<>();

        // Dynamic filters
        if (id != null) {
            predicates.add(cb.equal(root.get("id"), id));
        }
        if (SearchUtils.isNotBlank(email)) {
            if (SearchUtils.isQuoted(email)) {
                String exact = SearchUtils.stripQuotes(email);
                predicates.add(cb.like(root.get("email"), "%" + exact + "%")); // Full, strict match
            } else {
                String normalized = SearchUtils.normalizeString(email.toLowerCase());
                Expression<String> unaccentedEmail = cb.function(" ", String.class, cb.lower(root.get("email")));
                predicates.add(cb.like(unaccentedEmail, "%" + normalized + "%"));
            }
        }
        if (SearchUtils.isNotBlank(name)) {
            if (SearchUtils.isQuoted(name)) {
                String exact = SearchUtils.stripQuotes(name);
                Predicate nameMatch = cb.like(root.get("name"), "%" + exact + "%");
                Predicate surnameMatch = cb.like(root.get("surname"), "%" + exact + "%");
                predicates.add(cb.or(nameMatch, surnameMatch));
            } else {
                name = SearchUtils.normalizeString(name);
                String[] terms = name.toLowerCase().split("\\s+");
                for (String term : terms) {
                    Expression<String> unaccentedName = cb.function("unaccent", String.class, cb.lower(root.get("name")));
                    Expression<String> unaccentedSurname = cb.function("unaccent", String.class, cb.lower(root.get("surname")));
                    predicates.add(cb.or(
                            cb.like(unaccentedName, "%" + term + "%"),
                            cb.like(unaccentedSurname, "%" + term + "%")
                    ));
                }
            }
        }
        if (SearchUtils.isNotBlank(phone)) {
            predicates.add(cb.equal(root.get("phone"), phone));
        }
        if (accountState != null) {
            predicates.add(cb.equal(root.get("accountState"), accountState));
        }
        if (SearchUtils.isNotBlank(roleStr)) {
            if (SearchUtils.isQuoted(roleStr)) {
                String exact = SearchUtils.stripQuotes(roleStr);
                predicates.add(cb.equal(root.get("role"), exact)); // Full, strict match
            } else {
                String normalizedRole = SearchUtils.normalizeString(roleStr.toLowerCase());
                Expression<String> unaccentedRole = cb.function("unaccent", String.class, cb.lower(root.get("role")));
                predicates.add(cb.like(unaccentedRole, "%" + normalizedRole + "%"));
            }
        }
        if (office != null) {
            predicates.add(cb.equal(root.get("office"), office));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Sorting logic
        if (parameter != null) {
            Path<Object> sortingField = root.get(parameter.getFieldName());
            query.orderBy(order == Order.DESCENDING ? cb.desc(sortingField) : cb.asc(sortingField));
        }

        TypedQuery<UserEntity> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult(offset);
        typedQuery.setMaxResults(limit);

        return typedQuery.getResultList();
    }

    public long getTotalUserCount(Long id, String email, String name, String phone, AccountState accountState, String roleStr, Office office) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserEntity> root = query.from(UserEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(cb.equal(root.get("id"), id));
        }
        if (SearchUtils.isNotBlank(email)) {
            if (SearchUtils.isQuoted(email)) {
                String exact = SearchUtils.stripQuotes(email);
                predicates.add(cb.like(root.get("email"), "%" + exact + "%")); // Full, strict match
            } else {
                String normalized = SearchUtils.normalizeString(email.toLowerCase());
                Expression<String> unaccentedEmail = cb.function(" ", String.class, cb.lower(root.get("email")));
                predicates.add(cb.like(unaccentedEmail, "%" + normalized + "%"));
            }
        }
        if (SearchUtils.isNotBlank(name)) {
            if (SearchUtils.isQuoted(name)) {
                String exact = SearchUtils.stripQuotes(name);
                Predicate nameMatch = cb.like(root.get("name"), "%" + exact + "%");
                Predicate surnameMatch = cb.like(root.get("surname"), "%" + exact + "%");
                predicates.add(cb.or(nameMatch, surnameMatch));
            } else {
                name = SearchUtils.normalizeString(name);
                String[] terms = name.toLowerCase().split("\\s+");
                for (String term : terms) {
                    Expression<String> unaccentedName = cb.function("unaccent", String.class, cb.lower(root.get("name")));
                    Expression<String> unaccentedSurname = cb.function("unaccent", String.class, cb.lower(root.get("surname")));
                    predicates.add(cb.or(
                            cb.like(unaccentedName, "%" + term + "%"),
                            cb.like(unaccentedSurname, "%" + term + "%")
                    ));
                }
            }
        }
        if (phone != null && !phone.isEmpty()) {
            predicates.add(cb.equal(root.get("phone"), phone));
        }
        if (accountState != null) {
            predicates.add(cb.equal(root.get("accountState"), accountState));
        }
        if (SearchUtils.isNotBlank(roleStr)) {
            if (SearchUtils.isQuoted(roleStr)) {
                String exact = SearchUtils.stripQuotes(roleStr);
                predicates.add(cb.equal(root.get("role"), exact)); // Full, strict match
            } else {
                String normalizedRole = SearchUtils.normalizeString(roleStr.toLowerCase());
                Expression<String> unaccentedRole = cb.function("unaccent", String.class, cb.lower(root.get("role")));
                predicates.add(cb.like(unaccentedRole, "%" + normalizedRole + "%"));
            }
        }
        if (office != null) {
            predicates.add(cb.equal(root.get("office"), office));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.select(cb.count(root));

        return em.createQuery(query).getSingleResult();
    }
}