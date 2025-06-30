package pt.uc.dei.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.mappers.NotificationMapper;
import pt.uc.dei.repositories.MessageRepository;
import pt.uc.dei.repositories.NotificationRepository;
import pt.uc.dei.websocket.WsNotifications;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    WsNotifications wsNotifications;

    @Inject
    MessageRepository messageRepository;

    @Inject
    NotificationMapper notificationMapper;

    public NotificationService() {
    }

    /**
     * Creates and sends a new message notification based on a MessageDTO.
     * Checks unread messages, builds NotificationEntity, maps to NotificationDTO, and tries WebSocket delivery.
     * Falls back to persistence if WebSocket fails.
     */
    public boolean newMessageNotification(MessageDTO messageDTO) {
        try {
            Long senderId = messageDTO.getSenderId();
            Long recipientId = messageDTO.getRecipientId();
            int unreadCount = messageRepository.getUnreadMessageCount(recipientId, senderId);

            // Build NotificationEntity using the mapper
            NotificationEntity notificationEntity = notificationMapper.fromMessageDTO(messageDTO, unreadCount);
            notificationEntity.setCreationDate(LocalDateTime.now());
            notificationEntity.setRead(false);
            notificationEntity.setSeen(false);
            notificationRepository.persist(notificationEntity);

            // Map to NotificationDTO for WebSocket delivery
            NotificationDTO notificationDTO = notificationMapper.toDTO(notificationEntity);
            boolean delivered = wsNotifications.notifyUser(notificationDTO);
            if (!delivered) {
                logger.info("WebSocket delivery failed, notification persisted for userId {}", recipientId);
            }
            return true;
        } catch (Exception e) {
            logger.error("Error creating/sending new message notification", e);
            return false;
        }
    }

    @Transactional
    public List<NotificationDTO> getNotifications(Long userId) {
        try {
            List<NotificationEntity> notificationEntities = notificationRepository.getNotifications(userId);
            return notificationEntities.stream()
                    .map(notificationMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to get notifications for userId {}", userId, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Marks a notification as read for a user. Returns true if successful, false if not found or not updated.
     */
    @Transactional
    public boolean readNotification(Long notificationId, Long userId) {
        try {
            boolean exists = notificationRepository.isNotificationIdValid(notificationId, userId);
            if (!exists) {
                logger.warn("Notification {} does not exist for userId {}", notificationId, userId);
                return false;
            }
            boolean updated = notificationRepository.readNotification(notificationId, userId);
            if (!updated) {
                logger.error("Failed to mark notification {} as read for userId {}", notificationId, userId);
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Error reading notification {} for userId {}", notificationId, userId, e);
            return false;
        }
    }

    public int getTotalNotifications(Long id) {
        try {
            return notificationRepository.getTotalNotifications(id);
        } catch (Exception e) {
            logger.error("Failed to get notifications from user " + id);
            return 0;
        }
    }
}
