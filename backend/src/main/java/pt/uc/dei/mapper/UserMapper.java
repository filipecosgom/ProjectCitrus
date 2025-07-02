package pt.uc.dei.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.ManagerDTO;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.dtos.UserResponseDTO;
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
        componentModel = "jakarta",
        uses = {AppraisalMapper.class, FinishedCourseMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(source = "managerUser", target = "manager", qualifiedByName = "toManagerDto")
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "completedCourses", target = "completedCourses")
    UserDTO toDto(UserEntity userEntity);

    List<UserDTO> toDtoList(List<UserEntity> users);

    @InheritInverseConfiguration(name = "toDto")
    @Mapping(source = "manager", target = "managerUser", qualifiedByName = "toManagerEntity")
    @Mapping(target = "evaluationsReceived", ignore = true)
    @Mapping(target = "evaluationsGiven", ignore = true)
    @Mapping(target = "completedCourses", ignore = true)
    @Mapping(target = "userIsDeleted", ignore = true) // <-- updated this line
    UserEntity toEntity(UserDTO userDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    void updateUserFromDto(UserDTO dto, @MappingTarget UserEntity entity);

    @Named("toResponseDto")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "userIsAdmin", source = "userIsAdmin")
    @Mapping(target = "userIsManager", source = "userIsManager")
    @Mapping(target = "accountState", source = "accountState")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "hasAvatar", source = "hasAvatar")
    @Mapping(target = "lastSeen", source = "lastSeen")
    @Mapping(target = "onlineStatus", source = "onlineStatus")
    UserResponseDTO toUserResponseDto(UserEntity entity);

    @Named("toManagerDto")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "surname", target = "surname")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "hasAvatar", target = "hasAvatar")
    @Mapping(source = "email", target = "email")
    ManagerDTO toManagerDto(UserEntity managerUser);

    @Named("toManagerEntity")
    @Mapping(source = "id", target = "id")
    UserEntity toManagerEntity(ManagerDTO dto);
}