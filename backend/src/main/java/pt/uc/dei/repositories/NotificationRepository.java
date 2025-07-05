package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.NotificationEntity;
import java.util.Collections;
import java.util.List;

@Stateless
public class NotificationRepository extends AbstractRepository<NotificationEntity> {
    private static final Logger LOGGER = LogManager.getLogger(NotificationRepository.class);
    private static final long serialVersionUID = 1L;

    public NotificationRepository() {
        super(NotificationEntity.class);
    }

    public List<NotificationEntity> getNotifications(Long userId) {
        try {
            List<NotificationEntity> notificationEntities = em.createNamedQuery("NotificationEntity.getNotifications", NotificationEntity.class)
                    .setParameter("id", userId)
                    .getResultList();
            return notificationEntities;
        } catch (Exception e) {
            LOGGER.error("Error fetching notifications: ", e);
            return Collections.emptyList();
        }
    }

    public NotificationEntity getChatNotificationBetween(Long recipientId, Long senderId) {
        // Implement this method and its named query using user IDs
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public int getTotalNotifications(Long userId) {
        try {
            Long totalNotifications = em.createNamedQuery("NotificationEntity.getTotalNotifications", Long.class)
                    .setParameter("id", userId)
                    .getSingleResult();
            return totalNotifications.intValue();
        } catch (Exception e) {
            LOGGER.error("Error fetching notifications: ", e);
            return 0;
        }
    }

    public boolean readNotification(Long notificationId, Long recipientId) {
        try {
            int updated = em.createNamedQuery("NotificationEntity.readNotification")
                    .setParameter("notificationId", notificationId)
                    .setParameter("userId", recipientId)
                    .executeUpdate();
            return updated > 0;
        } catch (Exception e) {
            LOGGER.error("Error reading notification", e);
            return false;
        }
    }

    public boolean isNotificationIdValid(Long notificationId, Long recipientId) {
        try {
            boolean exists = !em.createNamedQuery("NotificationEntity.checkIfNotificationExist", NotificationEntity.class)
                    .setParameter("notificationId", notificationId)
                    .setParameter("userId", recipientId)
                    .getResultList().isEmpty();
            return exists;
        } catch (Exception e) {
            LOGGER.error("Error reading notification", e);
            return false;
        }
    }

    public boolean markMessageNotificationsAsRead(Long userId) {
        try {
            int updatedCount = em.createQuery(
                "UPDATE NotificationEntity n " +
                "SET n.notificationIsRead = true " +
                "WHERE n.user.id = :userId " +
                "AND n.type = :messageType " +
                "AND n.notificationIsRead = false"
            )
            .setParameter("userId", userId)
            .setParameter("messageType", NotificationType.MESSAGE)
            .executeUpdate();
            
            LOGGER.info("Marked {} MESSAGE notifications as read for user {}", updatedCount, userId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error marking MESSAGE notifications as read for user {}", userId, e);
            return false;
        }
    }
}
