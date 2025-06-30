package pt.uc.dei.mappers;

import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.enums.NotificationType;
import java.time.LocalDateTime;

public class NotificationMapper {
    public NotificationEntity fromMessageDTO(MessageDTO messageDTO, int unreadCount) {
        NotificationEntity entity = new NotificationEntity();
        entity.setSenderId(messageDTO.getSenderId());
        entity.setRecipientId(messageDTO.getRecipientId());
        entity.setType(NotificationType.MESSAGE);
        entity.setContent(messageDTO.getContent());
        entity.setTimestamp(LocalDateTime.now());
        entity.setRead(false);
        entity.setUnreadCount(unreadCount);
        return entity;
    }

    public NotificationDTO toDTO(NotificationEntity entity) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setSenderId(entity.getSenderId());
        dto.setRecipientId(entity.getRecipientId());
        dto.setType(entity.getType() != null ? entity.getType().name() : null);
        dto.setContent(entity.getContent());
        dto.setTimestamp(entity.getTimestamp());
        dto.setRead(entity.isRead());
        dto.setUnreadCount(entity.getUnreadCount());
        return dto;
    }
}
