
package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.NotificationEntity;
import java.util.Collections;
import java.util.List;
import pt.uc.dei.enums.NotificationType;

@Stateless
public class NotificationRepository extends AbstractRepository<NotificationEntity> {
    /**
     * Fetches a NotificationEntity by its unique identifier.
     *
     * @param id The notification ID
     * @return The NotificationEntity if found, or null otherwise
     */
    public NotificationEntity findById(Long id) {
        try {
            return em.find(NotificationEntity.class, id);
        } catch (Exception e) {
            LOGGER.error("Error fetching notification by id", e);
            return null;
        }
    }
    private static final Logger LOGGER = LogManager.getLogger(NotificationRepository.class);
    private static final long serialVersionUID = 1L;

    public NotificationRepository() {
        super(NotificationEntity.class);
    }

    /**
     * Retrieves all notifications for a given user.
     *
     * @param userId The ID of the user
     * @return List of NotificationEntity for the user, or empty if none found
     */
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


    /**
     * Returns the existing unread MESSAGE notification between recipient and sender, or null if none exists.
     *
     * @param recipientId The ID of the recipient user
     * @param senderId    The ID of the sender user
     * @return The NotificationEntity if found, or null otherwise
     */
    public NotificationEntity getMessageNotificationBetween(Long recipientId, Long senderId) {
        try {
            return em.createNamedQuery("NotificationEntity.getMessageNotification", NotificationEntity.class)
                .setParameter("recipientId", recipientId)
                .setParameter("senderId", senderId)
                .getResultStream()
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            LOGGER.error("Error fetching message notification between users", e);
            return null;
        }
    }

    /**
     * Gets the total number of notifications for a user.
     *
     * @param userId The ID of the user
     * @return The total number of notifications, or 0 if an error occurs
     */
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

    /**
     * Marks a notification as read for a given recipient.
     *
     * @param notificationId The ID of the notification
     * @param recipientId    The ID of the recipient user
     * @return true if the notification was marked as read, false otherwise
     */
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

    /**
     * Checks if a notification exists for a given recipient and notification ID.
     *
     * @param notificationId The ID of the notification
     * @param recipientId    The ID of the recipient user
     * @return true if the notification exists, false otherwise
     */
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

    /**
     * Marks all unread MESSAGE notifications as read for a given user.
     *
     * @param userId The ID of the user
     * @return true if the operation succeeded, false otherwise
     */
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

    /**
     * Fetches all MESSAGE notifications that are unread, unseen, and unemailed, and were created at least 24 hours ago.
     * Includes recipient and sender entities in the result.
     *
     * @return List of NotificationEntity matching the criteria, or empty if none found
     */
    public List<NotificationEntity> getUnemailedMessageNotifications() {
        try {
            return em.createQuery(
                "SELECT n FROM NotificationEntity n " +
                "JOIN FETCH n.user " +
                "JOIN FETCH n.sender " +
                "WHERE n.type = :type " +
                "AND n.notificationIsRead = false " +
                "AND n.notificationIsSeen = false " +
                "AND n.emailSent = false " +
                "AND n.creationDate <= :dateLimit",
                NotificationEntity.class
            )
            .setParameter("type", NotificationType.MESSAGE)
            .setParameter("dateLimit", java.time.LocalDateTime.now().minusHours(24))
            .getResultList();
        } catch (Exception e) {
            LOGGER.error("Error fetching old unread, unseen, unemailed MESSAGE notifications", e);
            return Collections.emptyList();
        }
    }
}
