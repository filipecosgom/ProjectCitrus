package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.CourseDTO;
import pt.uc.dei.dtos.CourseNewDTO;
import pt.uc.dei.dtos.CourseUpdateDTO;
import pt.uc.dei.entities.CourseEntity;
import java.util.List;

/**
 * MapStruct mapper interface for converting between {@link CourseEntity} and its DTO representations.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Automatic mapping between {@code CourseEntity}, {@code CourseDTO}, {@code CourseNewDTO}, and {@code CourseUpdateDTO}</li>
 *   <li>Handles mapping of collections (lists) of courses and DTOs</li>
 *   <li>Ignores userCompletions and admin fields to avoid persistence and security issues</li>
 *   <li>Provides update methods for partial entity updates from DTOs</li>
 * </ul>
 * <p>
 * <b>Usage:</b> This interface is implemented automatically by MapStruct at build time.
 * Inject or obtain an instance via CDI or the generated implementation for use in your service layer.
 *
 * @author ProjectCitrus Team
 * @version 1.0
 */
@Mapper(
        componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CourseMapper {

    /**
     * Maps a {@link CourseEntity} to a {@link CourseDTO}.
     *
     * @param courseEntity the entity to convert
     * @return the mapped DTO, or null if input is null
     */
    CourseDTO toDto(CourseEntity courseEntity);

    /**
     * Maps a {@link CourseDTO} to a {@link CourseEntity}.
     * Ignores userCompletions and admin fields; set these in the service layer if needed.
     *
     * @param courseDTO the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true) // Set admin in service if needed
    CourseEntity toEntity(CourseDTO courseDTO);

    /**
     * Maps a {@link CourseUpdateDTO} to a {@link CourseEntity}.
     * Ignores userCompletions and admin fields; set these in the service layer if needed.
     *
     * @param courseUpdateDTO the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    CourseEntity toEntity(CourseUpdateDTO courseUpdateDTO);

    /**
     * Maps a {@link CourseNewDTO} to a {@link CourseEntity}.
     * Ignores userCompletions, admin, and id fields; set these in the service layer if needed.
     *
     * @param courseNewDTO the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "id", ignore = true)
    CourseEntity toEntity(CourseNewDTO courseNewDTO);

    /**
     * Maps a list of {@link CourseEntity} objects to a list of {@link CourseDTO} objects.
     *
     * @param courses the list of entities to convert
     * @return the mapped list of DTOs (empty if input is empty or null)
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CourseDTO> toDtoList(List<CourseEntity> courses);

    /**
     * Maps a list of {@link CourseDTO} objects to a list of {@link CourseEntity} objects.
     *
     * @param courseDTOs the list of DTOs to convert
     * @return the mapped list of entities (empty if input is empty or null)
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CourseEntity> toEntityList(List<CourseDTO> courseDTOs);

    /**
     * Updates an existing {@link CourseEntity} with values from a {@link CourseDTO}.
     * <p>
     * Useful for partial updates while preserving existing entity relationships and IDs.
     * Ignores userCompletions and admin fields to avoid accidental changes.
     *
     * @param courseDTO the DTO containing the new values
     * @param courseEntity the existing entity to update (modified in place)
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    void updateEntityFromDto(CourseDTO courseDTO, @MappingTarget CourseEntity courseEntity);

    /**
     * Updates an existing {@link CourseEntity} with values from a {@link CourseUpdateDTO}.
     * <p>
     * Useful for partial updates while preserving existing entity relationships and IDs.
     * Ignores userCompletions and admin fields to avoid accidental changes.
     *
     * @param courseUpdateDTO the DTO containing the new values
     * @param courseEntity the existing entity to update (modified in place)
     */
    @Mapping(target = "userCompletions", ignore = true)
    @Mapping(target = "admin", ignore = true)
    void updateEntityFromUpdateDto(CourseUpdateDTO courseUpdateDTO, @MappingTarget CourseEntity courseEntity);
}
