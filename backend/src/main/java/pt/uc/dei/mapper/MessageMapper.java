package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.MessageSendDTO;
import pt.uc.dei.entities.MessageEntity;
import pt.uc.dei.entities.UserEntity;

@Mapper(componentModel = "jakarta")
public interface MessageMapper {
    @Mapping(target = "receiver", expression = "java(mapUserIdToUserEntity(dto.getReceiverId()))")
    @Mapping(target = "sender", expression = "java(mapUserIdToUserEntity(dto.getSenderId()))")
    MessageEntity toEntity(MessageDTO dto);

    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "senderId", source = "sender.id")
    MessageDTO toDto(MessageEntity entity);

    default UserEntity mapUserIdToUserEntity(Long userId) {
        if (userId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }
}