package pt.uc.dei.unit.mapper;

import pt.uc.dei.dtos.*;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.mapper.UserMapper;

import java.util.Collections;
import java.util.List;

public class UserMapperMock implements UserMapper {
    @Override public UserDTO toDto(UserEntity userEntity) { return null; }
    @Override public List<UserDTO> toDtoList(List<UserEntity> users) { return Collections.emptyList(); }
    @Override public UserEntity toEntity(UserDTO userDTO) { return new UserEntity(); }
    @Override public void updateUserFromDto(UserDTO dto, UserEntity entity) {}
    @Override public UserFullDTO toFullDto(UserEntity entity) { return null; }
    @Override public UserResponseDTO toUserResponseDto(UserEntity entity) { UserResponseDTO dto = new UserResponseDTO(); if (entity != null) dto.setId(entity.getId()); return dto; }
    @Override public ManagerDTO toManagerDto(UserEntity managerUser) { return null; }
    @Override public UserEntity toManagerEntity(ManagerDTO dto) { return null; }
}
