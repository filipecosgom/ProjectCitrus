package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;

import java.time.LocalDateTime;

@Mapper(componentModel = "jakarta", uses = UserMapper.class)
public interface NotificationMapper {
    @Mapping(target = "sender", source = "sender", qualifiedByName = "toResponseDto")
    @Mapping(target = "recipient", source = "user", qualifiedByName = "toResponseDto")
    @Mapping(source = "notificationIsRead", target = "notificationIsRead")
    @Mapping(source = "notificationIsSeen", target = "notificationIsSeen")
    NotificationDTO toDto(NotificationEntity entity);

    NotificationEntity toEntity(NotificationDTO dto);

    // For creating a NotificationEntity from a MessageDTO
    @Mapping(target = "sender", expression = "java(mapSenderIdToUser(messageDTO.getSenderId()))")
    @Mapping(target = "user", expression = "java(mapRecipientIdToUser(messageDTO.getRecipientId()))")
    @Mapping(target = "type", constant = "MESSAGE")
    @Mapping(target = "content", source = "messageDTO.content")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "notificationIsRead", constant = "false")
    @Mapping(target = "messageCount", source = "unreadCount")
    NotificationEntity fromMessageDTO(MessageDTO messageDTO, int unreadCount);


    default UserEntity mapRecipientIdToUser(Long recipientId) {
        if (recipientId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(recipientId);
        return user;
    }

    default UserEntity mapSenderIdToUser(Long senderId) {
        if (senderId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(senderId);
        return user;
    }

    @AfterMapping
    default void setDefaultValues(@MappingTarget NotificationEntity entity) {
        if (entity.getNotificationIsRead() == null) {
            entity.setNotificationIsRead(false);
        }
        if (entity.getTimestamp() == null) {
            entity.setTimestamp(LocalDateTime.now());
        }
    }
}
