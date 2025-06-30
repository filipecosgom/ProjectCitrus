package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.entity.NotificationEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Stateless
public class NotificationDao extends AbstractDao<NotificationEntity> {
    private static final Logger logger = LogManager.getLogger(NotificationDao.class);
    private static final long serialVersionUID = 1L;

    public NotificationDao() {
        super(NotificationEntity.class);
    }

    public List<NotificationEntity> getNotifications (String username) {
        try {
            List<NotificationEntity> notificationEntities = em.createNamedQuery("NotificationEntity.getNotifications", NotificationEntity.class).setParameter("username", username).getResultList();
            return notificationEntities;
        }
        catch (Exception e) {
            logger.error("Error fetching notifications: ", e);
            return Collections.emptyList();
        }
    }

    public NotificationEntity getChatNotificationBetween (String recipientUsername, String senderUsername) {
        try {
            NotificationEntity notificationEntity = em.createNamedQuery("NotificationEntity.getChatNotificationBetween", NotificationEntity.class)
                    .setParameter("recipientUsername", recipientUsername)
                    .setParameter("senderUsername", senderUsername)
                    .getSingleResult();
            return notificationEntity;
        }
        catch (Exception e) {
            logger.error("Error fetching notifications: ", e);
            return null;
        }
    }

    public int getTotalNotifications(String username) {
        try {
            Long totalNotifications = em.createNamedQuery("NotificationEntity.getTotalNotifications", Long.class)
                    .setParameter("username", username)
                    .getSingleResult(); // Since COUNT always returns one value
            return totalNotifications.intValue(); // Convert to int
        } catch (Exception e) {
            logger.error("Error fetching notifications: ", e);
            return 0; // Return 0 in case of failure
        }
    }

    public boolean readNotification (Long notificationId, String recipientUsername) {
        try {
            if (em.createNamedQuery("NotificationEntity.readNotification")
                    .setParameter("notificationId", notificationId)
                    .setParameter("recipientUsername", recipientUsername)
                    .executeUpdate() > 0) {
                return true;
            }
            else {
                return false;
            }
        }catch (Exception e) {
            logger.error("Error reading notification", e);
            return false;
        }
    }

    public boolean isNotificationIdValid (Long notificationId, String recipientUsername) {
        try {
            if (em.createNamedQuery("NotificationEntity.checkIfNotificationExist", NotificationEntity.class)
                    .setParameter("notificationId", notificationId)
                    .setParameter("recipientUsername", recipientUsername)
                    .getResultList().isEmpty()) {
                return false;
            }
            else {
                return true;
            }
        }catch (Exception e) {
            logger.error("Error reading notification", e);
            return false;
        }
    }

    public boolean hasRecipientBeenNotified(String senderUsername, String recipientUsername) {
        try {
            Long count = (Long) em.createNamedQuery("NotificationEntity.isRecipientNotified", Long.class)
                    .setParameter("senderUsername", senderUsername)
                    .setParameter("recipientUsername", recipientUsername)
                    .getSingleResult(); // If no result is found, it might throw NoResultException

            return count > 0; // If count > 0, recipient has been notified
        } catch (NoResultException e) {
            logger.error("No notifications found for recipient {} from sender {}", recipientUsername, senderUsername);
            return false;
        }
    }

    public boolean updateMessageNotification(String senderUsername, String recipientUsername, int numberUnreadMessages, String lastMessage) {
        try {
            if (em.createNamedQuery("NotificationEntity.updateMessageNotification")
                    .setParameter("senderUsername", senderUsername)
                    .setParameter("recipientUsername", recipientUsername)
                    .setParameter("numberUnreadMessages", numberUnreadMessages)
                    .setParameter("lastMessage", lastMessage)
                    .setParameter("newDate", LocalDateTime.now())  // Use LocalDateTime instead of LocalTime
                    .executeUpdate() > 0) {
                return true;
            } else {
                logger.error("Error updating notification: no rows affected");
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception occurred while updating notification", e);
            return false;
        }
    }
}
