package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;

import java.time.LocalDateTime;

@Mapper(componentModel = "jakarta")
public interface NotificationMapper {
    NotificationDTO toDto(NotificationEntity entity);
    NotificationEntity toEntity(NotificationDTO dto);

    @Mapping(target = "senderId", source = "messageDTO.senderId")
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
