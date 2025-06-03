package pt.uc.dei.mapper;
import org.mapstruct.*;
import pt.uc.dei.dtos.AppraisalDTO;
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
 * @author [Your Name]
 * @version 1.0
 */
@Mapper(
        componentModel = "jakarta",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AppraisalMapper {

    /**
     * Converts an AppraisalEntity object to an AppraisalDTO object.
     *
     * @param appraisalEntity the entity to convert
     * @return the mapped DTO
     */
    @Mapping(source = "appraisedUser.id", target = "appraisedUserId") // Maps appraised user ID
    @Mapping(source = "appraisingUser.id", target = "appraisingUserId") // Maps appraising user ID
    AppraisalDTO toDto(AppraisalEntity appraisalEntity);

    /**
     * Converts a list of AppraisalEntity objects into a list of AppraisalDTO objects.
     *
     * @param appraisals the list of entities to convert
     * @return the mapped list of DTOs
     */
    List<AppraisalDTO> toDtoList(List<AppraisalEntity> appraisals);

    /**
     * Converts an AppraisalDTO object back into an AppraisalEntity object.
     * Ignores user mapping to prevent circular references.
     *
     * @param appraisalDTO the DTO to convert
     * @return the mapped entity
     */
    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "appraisedUser", ignore = true) // Prevent recursion issues
    @Mapping(target = "appraisingUser", ignore = true)
    AppraisalEntity toEntity(AppraisalDTO appraisalDTO);

    /**
     * Updates an existing AppraisalEntity with non-null values from AppraisalDTO.
     * Useful for PATCH updates where only specific fields should be modified.
     *
     * @param dto    the DTO containing updated fields
     * @param entity the entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // ID should never be updated in PATCH requests
    @Mapping(target = "cycle", ignore = true) // Prevent cycle reference issues
    void updateAppraisalFromDto(AppraisalDTO dto, @MappingTarget AppraisalEntity entity);
}