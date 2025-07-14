package pt.uc.dei.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.ManagerDTO;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.dtos.UserFullDTO;
import pt.uc.dei.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import org.mapstruct.*;

/**
 * MapStruct mapper interface for converting between {@link UserEntity} and its DTO representations.
 * <p>
 * <ul>
 *   <li>Uses CDI component model for dependency injection.</li>
 *   <li>Handles nested mappings for manager, appraisals, and finished courses.</li>
 *   <li>Ignores sensitive fields (e.g., password) in DTOs for security.</li>
 *   <li>Prevents infinite recursion by using qualified mappings for manager fields.</li>
 *   <li>Supports partial updates via {@code updateUserFromDto}.</li>
 *   <li>Provides minimal and full DTO conversions for different use cases.</li>
 * </ul>
 * <b>Mapping summary:</b>
 * <ul>
 *   <li>{@code toDto}: Maps {@link UserEntity} to {@link UserDTO} (basic user info, manager as {@link ManagerDTO}).</li>
 *   <li>{@code toEntity}: Maps {@link UserDTO} to {@link UserEntity} (manager as UserEntity, ignores some fields).</li>
 *   <li>{@code updateUserFromDto}: Updates an existing {@link UserEntity} from a {@link UserDTO}, ignoring nulls and immutable fields.</li>
 *   <li>{@code toFullDto}: Maps {@link UserEntity} to {@link UserFullDTO} (includes appraisals and completed courses).</li>
 *   <li>{@code toUserResponseDto}: Maps {@link UserEntity} to {@link UserResponseDTO} (for API responses, includes online status, last seen, etc.).</li>
 *   <li>{@code toManagerDto}: Maps a manager {@link UserEntity} to {@link ManagerDTO} (for nested manager info).</li>
 *   <li>{@code toManagerEntity}: Maps {@link ManagerDTO} to a minimal {@link UserEntity} (for manager references).</li>
 * </ul>
 * <b>Note:</b> Uses {@link AppraisalMapper}, {@link FinishedCourseMapper}, and {@link CourseMapper} for nested mappings.
 */
@Mapper(
        componentModel = "cdi",
        uses = {AppraisalMapper.class, FinishedCourseMapper.class, CourseMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(source = "managerUser", target = "manager", qualifiedByName = "toManagerDto")
    @Mapping(target = "password", ignore = true)
    UserDTO toDto(UserEntity userEntity);

    List<UserDTO> toDtoList(List<UserEntity> users);

    @InheritInverseConfiguration(name = "toDto")
    @Mapping(source = "manager", target = "managerUser", qualifiedByName = "toManagerEntity")
    @Mapping(target = "evaluationsReceived", ignore = true)
    @Mapping(target = "evaluationsGiven", ignore = true)
    @Mapping(target = "completedCourses", ignore = true)
    @Mapping(target = "userIsDeleted", ignore = true) // <-- updated this line
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserDTO userDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    void updateUserFromDto(UserDTO dto, @MappingTarget UserEntity entity);

    @Named("toFullDto")
    @Mapping(target = "manager", source = "managerUser", qualifiedByName = "toManagerDto")
    @Mapping(target = "evaluationsReceived", source = "evaluationsReceived")
    @Mapping(target = "evaluationsGiven", source = "evaluationsGiven")
    @Mapping(target = "completedCourses", source = "completedCourses")
    @Mapping(target = "password", ignore = true)
    UserFullDTO toFullDto(UserEntity entity);

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
    @Mapping(target = "manager", source = "managerUser", qualifiedByName = "toManagerDto")
    @Mapping(target = "password", ignore = true)
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