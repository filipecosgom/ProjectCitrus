package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.entities.CycleEntity;

import java.util.List;

/**
 * MapStruct mapper interface for converting between {@link CycleEntity} and its DTO representations.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Automatic mapping between {@code CycleEntity}, {@code CycleDTO}, and {@code CycleUpdateDTO}</li>
 *   <li>Handles mapping of nested admin and evaluations fields, mapping admin to adminId</li>
 *   <li>Supports mapping of collections (lists) of cycles and DTOs</li>
 *   <li>Prevents circular references by mapping only IDs for admin</li>
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
        uses = { AppraisalMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CycleMapper {

    /**
     * Maps a {@link CycleEntity} to a {@link CycleDTO}.
     * <p>
     * Maps the admin relationship to just the admin ID to prevent circular references.
     * Includes evaluations as a list of DTOs.
     *
     * @param cycleEntity the entity to convert
     * @return the mapped DTO, or null if input is null
     */
    @Mapping(source = "admin.id", target = "adminId")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "evaluations", target = "evaluations")
    CycleDTO toDto(CycleEntity cycleEntity);

    /**
     * Maps a {@link CycleDTO} to a {@link CycleEntity}.
     * <p>
     * Ignores admin, state, id, and evaluations fields; set these in the service layer if needed.
     *
     * @param cycleDTO the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true) // ID should be auto-generated
    @Mapping(target = "evaluations", ignore = true) // Usually ignored during creation
    CycleEntity toEntity(CycleDTO cycleDTO);

    /**
     * Maps a list of {@link CycleEntity} objects to a list of {@link CycleDTO} objects.
     *
     * @param cycles the list of entities to convert
     * @return the mapped list of DTOs (empty if input is empty or null)
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CycleDTO> toDtoList(List<CycleEntity> cycles);

    /**
     * Maps a list of {@link CycleDTO} objects to a list of {@link CycleEntity} objects.
     *
     * @param cycleDTOs the list of DTOs to convert
     * @return the mapped list of entities (empty if input is empty or null)
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CycleEntity> toEntityList(List<CycleDTO> cycleDTOs);

    /**
     * Updates an existing {@link CycleEntity} with values from a {@link CycleUpdateDTO}.
     * <p>
     * Useful for partial updates while preserving existing entity relationships and IDs.
     * Ignores admin and id fields to avoid accidental changes.
     *
     * @param cycleUpdateDTO the DTO containing the new values
     * @param cycleEntity the existing entity to update (modified in place)
     */
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "state", target = "state")
    void updateEntityFromDto(CycleUpdateDTO cycleUpdateDTO, @MappingTarget CycleEntity cycleEntity);
}
