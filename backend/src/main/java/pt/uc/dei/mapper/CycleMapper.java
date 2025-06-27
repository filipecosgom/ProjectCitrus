package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.entities.CycleEntity;

import java.util.List;

/**
 * Mapper interface for converting CycleEntity objects into CycleDTO objects.
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
        uses = { AppraisalMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CycleMapper {

    /**
     * Converts a CycleEntity object to a CycleDTO object.
     * Maps the admin relationship to just the admin ID to prevent circular references.
     *
     * @param cycleEntity the entity to convert
     * @return the mapped DTO
     */
    @Mapping(source = "admin.id", target = "adminId")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "evaluations", target = "evaluations")
    CycleDTO toDto(CycleEntity cycleEntity);

    /**
     * Converts a CycleDTO object to a CycleEntity object.
     * Note: This mapping ignores the admin relationship to prevent issues.
     * Admin should be set separately in the service layer.
     *
     * @param cycleDTO the DTO to convert
     * @return the mapped entity
     */
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true) // ID should be auto-generated
    @Mapping(target = "evaluations", ignore = true) // Usually ignored during creation
    CycleEntity toEntity(CycleDTO cycleDTO);

    /**
     * Converts a list of CycleEntity objects into a list of CycleDTO objects.
     *
     * @param cycles the list of entities to convert
     * @return the mapped list of DTOs
     */
    List<CycleDTO> toDtoList(List<CycleEntity> cycles);

    /**
     * Converts a list of CycleDTO objects into a list of CycleEntity objects.
     *
     * @param cycleDTOs the list of DTOs to convert
     * @return the mapped list of entities
     */
    List<CycleEntity> toEntityList(List<CycleDTO> cycleDTOs);

    /**
     * Updates an existing CycleEntity with values from a CycleDTO.
     * Useful for partial updates while preserving existing relationships.
     *
     * @param cycleUpdateDTO the DTO containing the new values
     * @param cycleEntity the existing entity to update
     */
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "state", target = "state")
    void updateEntityFromDto(CycleUpdateDTO cycleUpdateDTO, @MappingTarget CycleEntity cycleEntity);
}
