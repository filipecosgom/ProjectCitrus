package pt.uc.dei.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.CourseDTO;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.repositories.CourseRepository;
import pt.uc.dei.mapper.CourseMapper;

import java.io.Serializable;
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

    /**
     * Retrieves courses with advanced filtering options.
     *
     * @param area      Optional filter by course area
     * @param language  Optional filter by course language
     * @param adminId   Optional filter by admin ID
     * @param isActive  Optional filter by active status
     * @param limit     Maximum number of results
     * @param offset    Starting position for pagination
     * @return Map containing filtered course DTOs and pagination info
     */
    public Map<String, Object> getCoursesWithFilters(CourseArea area, Language language, Long adminId, Boolean isActive, Integer limit, Integer offset) {
        LOGGER.debug("Retrieving courses with filters");

        List<CourseEntity> courses = courseRepository.findCoursesWithFilters(area, language, adminId, isActive, limit, offset);
        long totalCourses = courseRepository.countCoursesWithFilters(area, language, adminId, isActive);

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
}
