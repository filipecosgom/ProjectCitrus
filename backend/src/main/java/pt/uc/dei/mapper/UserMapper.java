package pt.uc.dei.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper interface for converting UserEntity objects into UserDTO objects.
 * <p>
 * - Uses Jakarta EE CDI (`componentModel = "jakarta"`) for automatic bean management.
 * - Handles nested mappings for `AppraisalMapper` and `FinishedCourseMapper`.
 * - Ensures sensitive fields like passwords are ignored.
 * - Prevents infinite recursion by excluding circular references.
 * - Provides a minimal DTO conversion for lightweight data transfer.
 * </p>
 */
@Mapper(
        componentModel = "jakarta", // Enables Jakarta EE CDI support
        uses = {AppraisalMapper.class, FinishedCourseMapper.class}, // Handles nested mappings
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE // Ignores null values
)
public interface UserMapper {

    /**
     * Converts a UserEntity object into a UserDTO.
     * <p>
     * - Maps the manager entity to its ID (`managerUser.id → managerId`).
     * - Excludes sensitive fields like `password`.
     * - Automatically maps completed courses using `FinishedCourseMapper`.
     * </p>
     *
     * @param userEntity The source UserEntity.
     * @return The mapped UserDTO.
     */
    @Mapping(source = "managerUser.id", target = "managerId") // Convert manager reference to ID
    @Mapping(target = "password", ignore = true) // Prevent exposure of sensitive password data
    @Mapping(source = "completedCourses", target = "completedCourses") // Use FinishedCourseMapper for nested mapping
    UserDTO toDto(UserEntity userEntity);

    /**
     * Converts a list of UserEntity objects into a list of UserDTO objects.
     * Automatically applies the `toDto()` mapping for each element.
     *
     * @param users The list of UserEntity objects.
     * @return The list of mapped UserDTO objects.
     */
    List<UserDTO> toDtoList(List<UserEntity> users);

    /**
     * Converts a UserDTO back into a UserEntity.
     * <p>
     * - Uses inverse mapping from `toDto()`.
     * - Prevents circular references by ignoring `evaluationsReceived` and `evaluationsGiven`.
     * - Excludes completed courses from being directly mapped.
     * - Ensures the deleted flag is not overwritten.
     * </p>
     *
     * @param userDTO The source UserDTO.
     * @return The mapped UserEntity.
     */
    @InheritInverseConfiguration(name = "toDto") // Reverse mapping
    @Mapping(target = "evaluationsReceived", ignore = true) // Prevent circular reference issues
    @Mapping(target = "evaluationsGiven", ignore = true)
    @Mapping(target = "completedCourses", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    UserEntity toEntity(UserDTO userDTO);

    /**
     * Updates an existing UserEntity with non-null values from UserDTO.
     * Used for PATCH updates where only specified fields should be modified.
     *
     * @param dto    The DTO containing updated fields.
     * @param entity The entity to update.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Ignore null values
    @Mapping(target = "id", ignore = true) // ID should never be updated
    @Mapping(target = "creationDate", ignore = true) // Prevent overwriting account creation timestamp
    void updateUserFromDto(UserDTO dto, @MappingTarget UserEntity entity);

    /**
     * Converts a UserEntity into a basic UserDTO with minimal data.
     * Used for lightweight transfers when full user details are not needed.
     *
     * @param entity The UserEntity.
     * @return The basic UserDTO.
     */
    @Named("toBasicDto") // Custom mapping for simplified DTO
    @Mapping(target = "email", source = "email")
    @Mapping(target = "admin", source = "admin")
    @Mapping(target = "manager", source = "manager")
    UserDTO toBasicDto(UserEntity entity);
}