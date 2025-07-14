package pt.uc.dei.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.FinishedCourseDTO;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.entities.FinishedCourseEntity;
import pt.uc.dei.entities.UserEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.*;

import javax.swing.plaf.IconUIResource;
import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper interface for converting between {@link FinishedCourseEntity} and {@link FinishedCourseDTO}.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Automatic mapping between entity and DTO, including nested user and course fields</li>
 *   <li>Handles mapping of collections (List and Set) of finished courses and DTOs</li>
 *   <li>Ignores null values for PATCH updates (partial updates)</li>
 *   <li>Prevents circular references by ignoring user field in inverse mapping</li>
 * </ul>
 * <p>
 * <b>Usage:</b> This interface is implemented automatically by MapStruct at build time.
 * Inject or obtain an instance via CDI or the generated implementation for use in your service layer.
 *
 * @author ProjectCitrus Team
 * @version 1.1
 */
@Mapper(
        componentModel = "cdi",
        uses = {CourseMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FinishedCourseMapper {

    /**
     * Maps a {@link FinishedCourseEntity} to a {@link FinishedCourseDTO}.
     * <p>
     * Maps nested user and course fields to flat DTO fields for API use.
     *
     * @param finishedCourseEntity the entity to convert
     * @return the mapped DTO, or null if input is null
     */
    @Mappings({
        @Mapping(source = "user.id", target = "userId"),
        @Mapping(source = "user.email", target = "userEmail"),
        @Mapping(source = "course.id", target = "courseId"),
        @Mapping(source = "course.title", target = "courseTitle"),
        @Mapping(source = "course.description", target = "courseDescription"),
        @Mapping(target = "courseArea", expression = "java(courseAreaToString(finishedCourseEntity.getCourse() != null ? finishedCourseEntity.getCourse().getArea() : null))"),
        @Mapping(source = "course.creationDate", target = "courseCreationDate"),
        @Mapping(source = "course.duration", target = "courseDuration"),
        @Mapping(target = "courseLanguage", expression = "java(finishedCourseEntity.getCourse() != null && finishedCourseEntity.getCourse().getLanguage() != null ? finishedCourseEntity.getCourse().getLanguage().getFieldName() : null)"),
        @Mapping(source = "course.link", target = "courseLink"),
        @Mapping(source = "course.courseHasImage", target = "courseHasImage"),
        @Mapping(source = "course.courseIsActive", target = "courseIsActive")
    })
    FinishedCourseDTO toDto(FinishedCourseEntity finishedCourseEntity);

    /**
     * Helper to map CourseArea enum to its fieldName (lowercase string) for DTO.
     */
    default String courseAreaToString(pt.uc.dei.enums.CourseArea area) {
        return area == null ? null : area.getFieldName();
    }

    /**
     * Maps a list of {@link FinishedCourseEntity} objects to a list of {@link FinishedCourseDTO} objects.
     *
     * @param finishedCourses the list of entities to convert
     * @return the mapped list of DTOs (empty if input is empty or null)
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FinishedCourseDTO> toDtoList(List<FinishedCourseEntity> finishedCourses);

    /**
     * Maps a set of {@link FinishedCourseEntity} objects to a set of {@link FinishedCourseDTO} objects.
     *
     * @param finishedCourses the set of entities to convert
     * @return the mapped set of DTOs (empty if input is empty or null)
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    Set<FinishedCourseDTO> toDtoSet(Set<FinishedCourseEntity> finishedCourses);

    /**
     * Maps a {@link FinishedCourseDTO} back to a {@link FinishedCourseEntity}.
     * <p>
     * Ignores mapping of the user field to prevent infinite recursion; set user in the service layer if needed.
     *
     * @param finishedCourseDTO the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "user", ignore = true) // Avoid infinite recursion
    FinishedCourseEntity toEntity(FinishedCourseDTO finishedCourseDTO);

    /**
     * Updates an existing {@link FinishedCourseEntity} with non-null values from a {@link FinishedCourseDTO}.
     * <p>
     * Useful for PATCH updates where only specific fields should be modified.
     * Ignores the ID field to prevent accidental changes.
     *
     * @param dto    the DTO containing updated fields
     * @param entity the entity to update (modified in place)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // ID should never be updated in PATCH requests
    void updateFinishedCourseFromDto(FinishedCourseDTO dto, @MappingTarget FinishedCourseEntity entity);
}