package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.CourseDTO;
import pt.uc.dei.dtos.CourseNewDTO;
import pt.uc.dei.dtos.CourseUpdateDTO;
import pt.uc.dei.entities.CourseEntity;
import java.util.List;

/**
 * Mapper interface for converting CourseEntity objects into CourseDTO objects and vice versa.
 * Uses MapStruct for automatic field mapping.
 */
@Mapper(
        componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CourseMapper {

    /**
     * Converts a CourseEntity object to a CourseDTO object.
     * @param courseEntity the entity to convert
     * @return the mapped DTO
     */
    CourseDTO toDto(CourseEntity courseEntity);

    /**
     * Converts a CourseDTO object to a CourseEntity object.
     * @param courseDTO the DTO to convert
     * @return the mapped entity
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true) // Set admin in service if needed
    CourseEntity toEntity(CourseDTO courseDTO);

    /**
     * Converts a CourseUpdateDTO object to a CourseEntity object.
     * @param courseUpdateDTO the DTO to convert
     * @return the mapped entity
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    CourseEntity toEntity(CourseUpdateDTO courseUpdateDTO);

    /**
     * Converts a CourseNewDTO object to a CourseEntity object.
     * @param courseNewDTO the DTO to convert
     * @return the mapped entity
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "id", ignore = true)
    CourseEntity toEntity(CourseNewDTO courseNewDTO);

    /**
     * Converts a list of CourseEntity objects into a list of CourseDTO objects.
     * @param courses the list of entities to convert
     * @return the mapped list of DTOs
     */
    List<CourseDTO> toDtoList(List<CourseEntity> courses);

    /**
     * Converts a list of CourseDTO objects into a list of CourseEntity objects.
     * @param courseDTOs the list of DTOs to convert
     * @return the mapped list of entities
     */
    List<CourseEntity> toEntityList(List<CourseDTO> courseDTOs);

    /**
     * Updates an existing CourseEntity with values from a CourseDTO.
     * Useful for partial updates while preserving existing relationships.
     * @param courseDTO the DTO containing the new values
     * @param courseEntity the existing entity to update
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    void updateEntityFromDto(CourseDTO courseDTO, @MappingTarget CourseEntity courseEntity);

    /**
     * Updates an existing CourseEntity with values from a CourseUpdateDTO.
     * Useful for partial updates while preserving existing relationships.
     * @param courseUpdateDTO the DTO containing the new values
     * @param courseEntity the existing entity to update
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    void updateEntityFromUpdateDto(CourseUpdateDTO courseUpdateDTO, @MappingTarget CourseEntity courseEntity);
}
