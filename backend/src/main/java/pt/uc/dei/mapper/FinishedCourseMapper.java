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
import java.util.List;
import java.util.Set;

/**
 * Mapper interface for converting FinishedCourseEntity objects into FinishedCourseDTO objects.
 * Uses MapStruct for automatic field mapping, ensuring clean transformations.
 *
 * <p>Supports:
 * <ul>
 *   <li>Automatic mapping between entity and DTO</li>
 *   <li>Handling collections (List and Set)</li>
 *   <li>Ignoring null values for PATCH updates</li>
 *   <li>Preventing circular references with UserEntity</li>
 * </ul>
 *
 * @author [Your Name]
 * @version 1.0
 */
@Mapper(
        componentModel = "jakarta",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FinishedCourseMapper {

    /**
     * Converts a FinishedCourseEntity object to a FinishedCourseDTO object.
     *
     * @param finishedCourseEntity the entity to convert
     * @return the mapped DTO
     */
    FinishedCourseDTO toDto(FinishedCourseEntity finishedCourseEntity);

    /**
     * Converts a list of FinishedCourseEntity objects into a list of FinishedCourseDTO objects.
     *
     * @param finishedCourses the list of entities to convert
     * @return the mapped list of DTOs
     */
    List<FinishedCourseDTO> toDtoList(List<FinishedCourseEntity> finishedCourses);

    /**
     * Converts a set of FinishedCourseEntity objects into a set of FinishedCourseDTO objects.
     *
     * @param finishedCourses the set of entities to convert
     * @return the mapped set of DTOs
     */
    Set<FinishedCourseDTO> toDtoSet(Set<FinishedCourseEntity> finishedCourses);

    /**
     * Converts a FinishedCourseDTO object back into a FinishedCourseEntity object.
     * Ignores mapping of the user field to prevent infinite recursion.
     *
     * @param finishedCourseDTO the DTO to convert
     * @return the mapped entity
     */
    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "user", ignore = true) // Avoid infinite recursion
    FinishedCourseEntity toEntity(FinishedCourseDTO finishedCourseDTO);

    /**
     * Updates an existing FinishedCourseEntity with non-null values from FinishedCourseDTO.
     * Useful for PATCH updates where only specific fields should be modified.
     *
     * @param dto    the DTO containing updated fields
     * @param entity the entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // ID should never be updated in PATCH requests
    void updateFinishedCourseFromDto(FinishedCourseDTO dto, @MappingTarget FinishedCourseEntity entity);
}