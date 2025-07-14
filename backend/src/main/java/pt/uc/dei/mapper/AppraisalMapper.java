package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.AppraisalResponseDTO;
import pt.uc.dei.entities.AppraisalEntity;

import java.util.List;

/**
 * MapStruct mapper interface for converting between {@link AppraisalEntity} and its DTO representations.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Automatic mapping between {@code AppraisalEntity}, {@code AppraisalDTO}, and {@code AppraisalResponseDTO}</li>
 *   <li>Handles mapping of nested user and cycle objects, including their IDs and response DTOs</li>
 *   <li>Supports mapping of collections (lists) of appraisals and DTOs</li>
 *   <li>Prevents circular references by using qualified mappings for user fields</li>
 *   <li>Ignores sensitive or unnecessary fields as needed for security and clarity</li>
 *   <li>Provides update methods for partial entity updates from DTOs</li>
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
        uses = {UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppraisalMapper {

    /**
     * Maps an {@link AppraisalEntity} to an {@link AppraisalDTO}.
     * <p>
     * Maps user and cycle relationships to their respective IDs and response DTOs.
     *
     * @param appraisalEntity the entity to convert
     * @return the mapped DTO, or null if input is null
     */
    @Mapping(source = "appraisedUser.id", target = "appraisedUserId")
    @Mapping(source = "appraisingUser.id", target = "appraisingUserId")
    @Mapping(source = "cycle.id", target = "cycleId")
    @Mapping(source = "appraisedUser", target = "appraisedUser", qualifiedByName = "toResponseDto")
    @Mapping(source = "appraisingUser", target = "appraisingUser", qualifiedByName = "toResponseDto")
    AppraisalDTO toDto(AppraisalEntity appraisalEntity);

    /**
     * Maps an {@link AppraisalEntity} to an {@link AppraisalResponseDTO}.
     * <p>
     * Includes cycle end date and full user objects for richer API responses.
     *
     * @param appraisalEntity the entity to convert
     * @return the mapped response DTO, or null if input is null
     */
    @Mapping(source = "cycle.id", target = "cycleId")
    @Mapping(source = "cycle.endDate", target = "endDate")
    @Mapping(source = "appraisedUser", target = "appraisedUser")
    @Mapping(source = "appraisingUser", target = "appraisingUser")
    AppraisalResponseDTO toResponseDto(AppraisalEntity appraisalEntity);

    /**
     * Maps an {@link AppraisalDTO} to an {@link AppraisalEntity}.
     * <p>
     * Ignores user and cycle relationships to avoid unintended persistence issues;
     * these should be set explicitly in the service layer.
     *
     * @param appraisalDTO the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    @Mapping(target = "appraisedUser", ignore = true)
    @Mapping(target = "appraisingUser", ignore = true)
    @Mapping(target = "cycle", ignore = true)
    @Mapping(target = "editedDate", ignore = true)
    @Mapping(target = "id", ignore = true) // ID should be auto-generated
    AppraisalEntity toEntity(AppraisalDTO appraisalDTO);


    /**
     * Maps a list of {@link AppraisalEntity} objects to a list of {@link AppraisalDTO} objects.
     * Returns an empty list if input is null.
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<AppraisalDTO> toDtoList(List<AppraisalEntity> appraisals);

    /**
     * Maps a list of {@link AppraisalDTO} objects to a list of {@link AppraisalEntity} objects.
     * Returns an empty list if input is null.
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<AppraisalEntity> toEntityList(List<AppraisalDTO> appraisalDTOs);

    /**
     * Updates an existing {@link AppraisalEntity} with values from an {@link AppraisalDTO}.
     * <p>
     * Useful for partial updates while preserving existing entity relationships and IDs.
     * Ignores user, cycle, and ID fields to avoid accidental changes.
     *
     * @param appraisalDTO the DTO containing the new values
     * @param appraisalEntity the existing entity to update (modified in place)
     */
    @Mapping(target = "appraisedUser", ignore = true)
    @Mapping(target = "appraisingUser", ignore = true)
    @Mapping(target = "cycle", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "editedDate", ignore = true)
    void updateEntityFromDto(AppraisalDTO appraisalDTO, @MappingTarget AppraisalEntity appraisalEntity);
}