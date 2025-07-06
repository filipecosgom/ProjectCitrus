package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.AppraisalResponseDTO;
import pt.uc.dei.entities.AppraisalEntity;

import java.util.List;

/**
 * Mapper interface for converting AppraisalEntity objects into AppraisalDTO objects.
 * Uses MapStruct for automatic field mapping and ensures proper handling of relations.
 *
 * <p>Supports:
 * <ul>
 *   <li>Automatic mapping between entity and DTO</li>
 *   <li>Handling collections (Lists)</li>
 *   <li>Preventing circular references with UserEntity</li>
 *   <li>Safely ignoring sensitive or unnecessary fields</li>
 * </ul>
 *
 * @author ProjectCitrus Team
 * @version 1.0
 */
@Mapper(
        componentModel = "cdi",
        uses = {UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppraisalMapper {

    /**
     * Converts an AppraisalEntity object to an AppraisalDTO object.
     *
     * @param appraisalEntity the entity to convert
     * @return the mapped DTO
     */
    @Mapping(source = "appraisedUser.id", target = "appraisedUserId")
    @Mapping(source = "appraisingUser.id", target = "appraisingUserId")
    @Mapping(source = "cycle.id", target = "cycleId")
    // CORREÇÃO: Remover qualifiedByName e deixar o MapStruct mapear automaticamente
    @Mapping(source = "appraisedUser", target = "appraisedUser")
    @Mapping(source = "appraisingUser", target = "appraisingUser")
    AppraisalDTO toDto(AppraisalEntity appraisalEntity);

    @Mapping(source = "cycle.id", target = "cycleId")
    @Mapping(source = "cycle.endDate", target = "endDate")
    @Mapping(source = "appraisedUser", target = "appraisedUser")
    @Mapping(source = "appraisingUser", target = "appraisingUser")
    AppraisalResponseDTO toResponseDto(AppraisalEntity appraisalEntity);

    /**
     * Converts an AppraisalDTO object to an AppraisalEntity object.
     * Note: This mapping ignores the user and cycle relationships to prevent issues.
     * These should be set separately in the service layer.
     *
     * @param appraisalDTO the DTO to convert
     * @return the mapped entity
     */
    @Mapping(target = "appraisedUser", ignore = true)
    @Mapping(target = "appraisingUser", ignore = true)
    @Mapping(target = "cycle", ignore = true)
    @Mapping(target = "editedDate", ignore = true)
    @Mapping(target = "id", ignore = true) // ID should be auto-generated
    AppraisalEntity toEntity(AppraisalDTO appraisalDTO);

    /**
     * Converts a list of AppraisalEntity objects into a list of AppraisalDTO objects.
     *
     * @param appraisals the list of entities to convert
     * @return the mapped list of DTOs
     */
    List<AppraisalDTO> toDtoList(List<AppraisalEntity> appraisals);

    /**
     * Converts a list of AppraisalDTO objects into a list of AppraisalEntity objects.
     *
     * @param appraisalDTOs the list of DTOs to convert
     * @return the mapped list of entities
     */
    List<AppraisalEntity> toEntityList(List<AppraisalDTO> appraisalDTOs);

    /**
     * Updates an existing AppraisalEntity with values from an AppraisalDTO.
     * Useful for partial updates while preserving existing relationships.
     *
     * @param appraisalDTO the DTO containing the new values
     * @param appraisalEntity the existing entity to update
     */
    @Mapping(target = "appraisedUser", ignore = true)
    @Mapping(target = "appraisingUser", ignore = true)
    @Mapping(target = "cycle", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "editedDate", ignore = true)
    void updateEntityFromDto(AppraisalDTO appraisalDTO, @MappingTarget AppraisalEntity appraisalEntity);
}