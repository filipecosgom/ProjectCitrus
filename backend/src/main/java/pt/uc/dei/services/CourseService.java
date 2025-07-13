package pt.uc.dei.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import pt.uc.dei.dtos.CourseDTO;
import pt.uc.dei.dtos.CourseNewDTO;
import pt.uc.dei.dtos.CourseUpdateDTO;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.CourseParameter;
import pt.uc.dei.enums.Language;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.enums.OrderBy;
import pt.uc.dei.repositories.CourseRepository;
import pt.uc.dei.mapper.CourseMapper;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.UserRepository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing course-related operations.
 * <p>
 * Provides functionality for course retrieval and filtering.
 * Handles business logic and validation rules.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
 */
@Stateless
public class CourseService implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(CourseService.class);
    private static final long serialVersionUID = 1L;

    @EJB
    private CourseRepository courseRepository;

    @Inject
    private CourseMapper courseMapper;

    @EJB
    private UserRepository userRepository;

    /**
     * Retrieves courses with advanced filtering options.
     *
     * @param area      Optional filter by course area
     * @param language  Optional filter by course language
     * @param adminName   Optional filter by admin name
     * @param courseIsActive  Optional filter by active status
     * @param limit     Maximum number of results
     * @param offset    Starting position for pagination
     * @return Map containing filtered course DTOs and pagination info
     */
    public Map<String, Object> getCoursesWithFilters(
            Long id, String title, Integer duration, String description,
            CourseArea area, Language language, String adminName, Boolean courseIsActive,
            CourseParameter parameter, OrderBy orderBy, Integer offset, Integer limit, Long excludeCompletedByUserId,
            List<Long> excludeCourseIds) {
        LOGGER.debug("Retrieving courses with filters");

        List<CourseEntity> courses = courseRepository.findCoursesWithFilters(
                id, title, duration, description, area, language, adminName, courseIsActive,
                parameter, orderBy, offset, limit, excludeCompletedByUserId, excludeCourseIds);
        long totalCourses = courseRepository.countCoursesWithFilters(
                id, title, duration, description, area, language, adminName, courseIsActive, excludeCompletedByUserId, excludeCourseIds);

        List<CourseDTO> courseDTOs = courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("courses", courseDTOs);
        responseData.put("totalCourses", totalCourses);
        responseData.put("offset", offset);
        responseData.put("limit", limit);
        return responseData;
    }

    /**
     * Creates a new course if the title and link do not already exist.
     * @param dto the CourseNewDTO containing course data (must include adminId)
     * @return CourseDTO if the course was created, null if a course with the same title or link exists or admin not found
     * @throws IllegalArgumentException with message "duplicateTitle" or "duplicateLink" if duplicate found
     */
    @Transactional
    public CourseDTO createNewCourse(CourseNewDTO dto, Long adminId) {
        if (dto == null || dto.getTitle() == null || adminId == null) {
            LOGGER.warn("Attempted to create course with null DTO, title, or adminId");
            return null;
        }
        if (courseRepository.existsByTitle(dto.getTitle())) {
            LOGGER.info("Course with title '{}' already exists", dto.getTitle());
            throw new IllegalArgumentException("duplicateTitle");
        }
        if (courseRepository.existsByLink(dto.getLink())) {
            LOGGER.info("Course with link '{}' already exists", dto.getLink());
            throw new IllegalArgumentException("duplicateLink");
        }
        UserEntity admin = userRepository.findUserById(adminId);
        if (admin == null) {
            LOGGER.warn("Admin user with id {} not found", adminId);
            return null;
        }
        CourseEntity entity = courseMapper.toEntity(dto);
        entity.setCreationDate(java.time.LocalDate.now());
        entity.setAdmin(admin);
        courseRepository.persist(entity);
        LOGGER.info("Created new course with title '{}' and admin id {}", dto.getTitle(), adminId);
        return courseMapper.toDto(entity);
    }

    /**
     * Updates an existing course with new data.
     * @param dto the CourseUpdateDTO containing updated course data (must include course id)
     * @return true if the course was updated, false if not found
     */
    @Transactional
    public boolean updateCourse(CourseUpdateDTO dto) {
        if (dto == null || dto.getId() == null) {
            LOGGER.warn("Attempted to update course with null DTO or id");
            return false;
        }
        CourseEntity entity = courseRepository.findCourseById(dto.getId());
        if (entity == null) {
            LOGGER.warn("Course with id {} not found", dto.getId());
            return false;
        }
        if (courseRepository.existsByTitle(dto.getTitle())) {
            LOGGER.info("Course with title '{}' already exists", dto.getTitle());
            throw new IllegalArgumentException("duplicateTitle");
        }
        if (courseRepository.existsByLink(dto.getLink())) {
            LOGGER.info("Course with link '{}' already exists", dto.getLink());
            throw new IllegalArgumentException("duplicateLink");
        }
        // Use MapStruct mapper for partial update
        courseMapper.updateEntityFromUpdateDto(dto, entity);
        courseRepository.persist(entity);
        LOGGER.info("clientIP = {}", ThreadContext.get("clientIP"));
        LOGGER.info("Updated course with id {} (via mapper)", dto.getId());
        return true;
    }

    public long countAllCourses() {
        return courseRepository.countCoursesWithFilters(null, null, null, null, null, null, null, null, null, null);
    }

    public long countCoursesByActive(boolean active) {
        return courseRepository.countCoursesWithFilters(null, null, null, null, null, null, null, active, null, null);
    }
}
