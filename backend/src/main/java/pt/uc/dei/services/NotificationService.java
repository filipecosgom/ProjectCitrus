
package pt.uc.dei.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.dtos.NotificationUpdateDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.AppraisalEntity;
import pt.uc.dei.entities.CycleEntity;
import pt.uc.dei.entities.FinishedCourseEntity;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.NotificationType;
import pt.uc.dei.mapper.NotificationMapper;
import pt.uc.dei.repositories.MessageRepository;
import pt.uc.dei.repositories.NotificationRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.websocket.WsNotifications;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for creating, sending, and managing user notifications.
 * Handles notifications for messages, appraisals, courses, cycles, and user updates.
 * Integrates with WebSocket for real-time delivery and persists notifications as fallback.
 */
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

    @Inject
    UserRepository userRepository;

    /**
     * Default constructor for NotificationService.
     */
    public NotificationService() {
    }

    /**
     * Creates and sends a new message notification based on a MessageDTO.
     * <p>
     * Checks unread messages, builds NotificationEntity, maps to NotificationDTO,
     * and tries WebSocket delivery. Falls back to persistence if WebSocket fails.
     *
     * @param messageDTO the message data transfer object containing sender and recipient info
     * @return true if notification was created and sent/persisted successfully, false otherwise
     */
    public boolean newMessageNotification(MessageDTO messageDTO) {
        try {
            Long senderId = messageDTO.getSenderId();
            Long recipientId = messageDTO.getRecipientId();
            int unreadCount = messageRepository.getUnreadMessageCount(recipientId, senderId);
            UserEntity recipientUser = userRepository.findUserById(recipientId);
            if (recipientUser == null) {
                logger.error("Recipient user {} not found", recipientId);
                return false;
            }
            UserEntity senderUser = userRepository.findUserById(senderId);
            if (senderUser == null) {
                logger.error("Recipient user {} not found", senderUser);
                return false;
            }
            // Try to get existing unread MESSAGE notification between these users
            NotificationEntity notificationEntity = notificationRepository.getMessageNotificationBetween(recipientId,
                    senderId);
            if (notificationEntity == null) {
                // No existing unread notification, create new
                notificationEntity = new NotificationEntity();
                notificationEntity.setSender(senderUser);
                notificationEntity.setUser(recipientUser); // Set managed entity
                notificationEntity.setType(NotificationType.MESSAGE);
                notificationEntity.setContent(messageDTO.getContent());
                notificationEntity.setCreationDate(LocalDateTime.now());
                notificationEntity.setNotificationIsRead(false);
                notificationEntity.setNotificationIsSeen(false);
                notificationEntity.setMessageCount(unreadCount);
                notificationRepository.persist(notificationEntity);
            } else {
                // Update existing notification
                notificationEntity.setContent(messageDTO.getContent());
                notificationEntity.setMessageCount(unreadCount);
                notificationEntity.setCreationDate(LocalDateTime.now());
                notificationEntity.setNotificationIsRead(false);
                notificationEntity.setNotificationIsSeen(false);
                // No need to persist explicitly if managed, but can call merge if needed
                // notificationRepository.merge(notificationEntity);
            }

            // Map to NotificationDTO for WebSocket delivery
            NotificationDTO notificationDTO = notificationMapper.toDto(notificationEntity);
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

    /**
     * Retrieves all notifications for a given user.
     *
     * @param userId the ID of the user
     * @return list of NotificationDTOs for the user, or empty list on error
     */
    @Transactional
    public List<NotificationDTO> getNotifications(Long userId) {
        try {
            List<NotificationEntity> notificationEntities = notificationRepository.getNotifications(userId);
            return notificationEntities.stream()
                    .map(notificationMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to get notifications for userId {}", userId, e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Updates notification status (isRead, isSeen) for a user.
     *
     * @param updateDTO the DTO containing update information
     * @param userId the ID of the user
     * @return true if update was successful, false if not found or not updated
     */
    @Transactional
    public boolean updateNotificationStatus(NotificationUpdateDTO updateDTO, Long userId) {
        try {
            Long notificationId = updateDTO.getNotificationId();
            NotificationEntity notification = notificationRepository.findById(notificationId);
            if (notification == null || !notification.getUser().getId().equals(userId)) {
                logger.warn("Notification {} does not exist for userId {}", notificationId, userId);
                return false;
            }
            boolean changed = false;
            if (updateDTO.getNotificationIsRead() != null) {
                notification.setNotificationIsRead(updateDTO.getNotificationIsRead());
                changed = true;
            }
            if (updateDTO.getNotificationIsSeen() != null) {
                notification.setNotificationIsSeen(updateDTO.getNotificationIsSeen());
                notification.setMessageCount(0);
                changed = true;
            }
            if (changed) {
                notificationRepository.merge(notification);
            }
            return changed;
        } catch (Exception e) {
            logger.error("Error updating notification status {} for userId {}", updateDTO.getNotificationId(), userId,
                    e);
            return false;
        }
    }

    /**
     * Gets the total number of notifications for a user.
     *
     * @param id the user ID
     * @return total number of notifications, or 0 on error
     */
    public int getTotalNotifications(Long id) {
        try {
            return notificationRepository.getTotalNotifications(id);
        } catch (Exception e) {
            logger.error("Failed to get notifications from user " + id);
            return 0;
        }
    }

    /**
     * Marks all message notifications as read for a user.
     *
     * @param userId the user ID
     * @return true if successful, false otherwise
     */
    public boolean markMessageNotificationsAsRead(Long userId) {
        try {
            return notificationRepository.markMessageNotificationsAsRead(userId);
        } catch (Exception e) {
            logger.error("Error marking message notifications as read for user {}", userId, e);
            return false;
        }
    }

    /**
     * Creates and sends a new appraisal notification.
     * <p>
     * The recipient is the appraisedUser, sender is the appraisingUser, content is
     * the score (as string or "N/A").
     *
     * @param appraisal the appraisal entity containing users and score
     * @return true if notification was created and sent/persisted successfully, false otherwise
     */
    @Transactional
    public boolean newAppraisalNotification(AppraisalEntity appraisal) {
        try {
            if (appraisal == null || appraisal.getAppraisedUser() == null || appraisal.getAppraisingUser() == null) {
                logger.error("Invalid appraisal or users for notification");
                return false;
            }
            Long recipientId = appraisal.getAppraisedUser().getId();
            Long senderId = appraisal.getAppraisingUser().getId();
            UserEntity recipientUser = userRepository.findUserById(recipientId);
            if (recipientUser == null) {
                logger.error("Appraised user {} not found", recipientId);
                return false;
            }
            UserEntity senderUser = userRepository.findUserById(senderId);
            if (senderUser == null) {
                logger.error("Appraised user {} not found", senderId);
                return false;
            }

            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setSender(senderUser);
            notificationEntity.setUser(recipientUser);
            notificationEntity.setType(NotificationType.APPRAISAL);
            String scoreStr = appraisal.getScore() != null ? String.valueOf(appraisal.getScore()) : "N/A";
            notificationEntity.setContent(scoreStr);
            notificationEntity.setCreationDate(LocalDateTime.now());
            notificationEntity.setNotificationIsRead(false);
            notificationEntity.setNotificationIsSeen(false);
            notificationEntity.setMessageCount(0); // Not relevant for appraisal
            notificationRepository.persist(notificationEntity);

            NotificationDTO notificationDTO = notificationMapper.toDto(notificationEntity);
            boolean delivered = wsNotifications.notifyUser(notificationDTO);
            if (!delivered) {
                logger.info("WebSocket delivery failed, appraisal notification persisted for userId {}", recipientId);
            }
            return true;
        } catch (Exception e) {
            logger.error("Error creating/sending new appraisal notification", e);
            return false;
        }
    }

    /**
     * Creates and sends a new course notification.
     * <p>
     * The recipient is the user, sender is the user's manager, content is the course id.
     *
     * @param finishedCourse the finished course entity
     * @return true if notification was created and sent/persisted successfully, false otherwise
     */
    @Transactional
    public boolean newCourseNotification(FinishedCourseEntity finishedCourse) {
        try {
            if (finishedCourse == null || finishedCourse.getUser() == null
                    || finishedCourse.getUser().getManager() == null) {
                logger.error("Invalid finished course or users for notification");
                return false;
            }
            Long recipientId = finishedCourse.getUser().getId();
            Long senderId = finishedCourse.getUser().getManager().getId();
            UserEntity recipientUser = userRepository.findUserById(recipientId);
            if (recipientUser == null) {
                logger.error("Course recipient user {} not found", recipientId);
                return false;
            }
            UserEntity sender = userRepository.findUserById(senderId);
            if (sender == null) {
                logger.error("Course recipient user {} not found", senderId);
                return false;
            }

            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setSender(sender);
            notificationEntity.setUser(recipientUser);
            notificationEntity.setType(NotificationType.COURSE);
            String courseIdStr = finishedCourse.getCourse() != null ? String.valueOf(finishedCourse.getCourse().getTitle())
                    : "N/A";
            notificationEntity.setContent(courseIdStr);
            notificationEntity.setCreationDate(LocalDateTime.now());
            notificationEntity.setNotificationIsRead(false);
            notificationEntity.setNotificationIsSeen(false);
            notificationEntity.setMessageCount(0); // Not relevant for course
            notificationRepository.persist(notificationEntity);

            NotificationDTO notificationDTO = notificationMapper.toDto(notificationEntity);
            boolean delivered = wsNotifications.notifyUser(notificationDTO);
            if (!delivered) {
                logger.info("WebSocket delivery failed, course notification persisted for userId {}", recipientId);
            }
            return true;
        } catch (Exception e) {
            logger.error("Error creating/sending new course notification", e);
            return false;
        }
    }

    /**
     * Creates and sends a new cycle open notification to a list of users.
     * <p>
     * For each user, creates a CYCLE_OPEN notification with the cycle end date as content.
     *
     * @param cycle the cycle entity
     * @param users the list of users to notify
     */
    @Transactional
    public void newCycleOpenNotification(CycleEntity cycle, List<UserEntity> users) {
        try {
            if (cycle == null || cycle.getAdmin() == null || users == null) {
                logger.error("Invalid cycle or user list for cycle notification");
                return;
            }
            Long senderId = cycle.getAdmin().getId();
            String endDateStr = cycle.getEndDate() != null ? cycle.getEndDate().toString() : "N/A";
            for (UserEntity user : users) {
                if (user == null)
                    continue;
                Long recipientId = user.getId();
                UserEntity recipientUser = userRepository.findUserById(recipientId);
                if (recipientUser == null) {
                    logger.error("Cycle notification recipient user {} not found", recipientId);
                    continue;
                }
                UserEntity sender = userRepository.findUserById(senderId);
                if (sender == null) {
                    logger.error("Cycle notification recipient user {} not found", senderId);
                    continue;
                }

                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setSender(sender);
                notificationEntity.setUser(recipientUser);
                notificationEntity.setType(NotificationType.CYCLE_OPEN);
                notificationEntity.setContent(endDateStr);
                notificationEntity.setCreationDate(LocalDateTime.now());
                notificationEntity.setNotificationIsRead(false);
                notificationEntity.setNotificationIsSeen(false);
                notificationEntity.setMessageCount(0); // Not relevant for cycle
                notificationRepository.persist(notificationEntity);

                NotificationDTO notificationDTO = notificationMapper.toDto(notificationEntity);
                boolean delivered = wsNotifications.notifyUser(notificationDTO);
                if (!delivered) {
                    logger.info("WebSocket delivery failed, cycle notification persisted for userId {}", recipientId);
                }
            }
        } catch (Exception e) {
            logger.error("Error creating/sending new cycle notifications", e);
        }
    }

    /**
     * Creates and sends a new cycle close notification to a list of users.
     * <p>
     * For each user, creates a CYCLE_CLOSE notification with the cycle end date as content.
     *
     * @param cycle the cycle entity
     * @param users the list of users to notify
     */
    @Transactional
    public void newCycleCloseNotification(CycleEntity cycle, List<UserEntity> users) {
        try {
            if (cycle == null || cycle.getAdmin() == null || users == null) {
                logger.error("Invalid cycle or user list for cycle notification");
                return;
            }
            Long senderId = cycle.getAdmin().getId();
            String endDateStr = cycle.getEndDate() != null ? cycle.getEndDate().toString() : "N/A";
            for (UserEntity user : users) {
                if (user == null)
                    continue;
                Long recipientId = user.getId();
                UserEntity recipientUser = userRepository.findUserById(recipientId);
                if (recipientUser == null) {
                    logger.error("Cycle notification recipient user {} not found", recipientId);
                    continue;
                }
                UserEntity sender = userRepository.findUserById(senderId);
                if (sender == null) {
                    logger.error("Cycle notification recipient user {} not found", senderId);
                    continue;
                }
                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setSender(sender);
                notificationEntity.setUser(recipientUser);
                notificationEntity.setType(NotificationType.CYCLE_CLOSE);
                notificationEntity.setContent(endDateStr);
                notificationEntity.setCreationDate(LocalDateTime.now());
                notificationEntity.setNotificationIsRead(false);
                notificationEntity.setNotificationIsSeen(false);
                notificationEntity.setMessageCount(0); // Not relevant for cycle
                notificationRepository.persist(notificationEntity);

                NotificationDTO notificationDTO = notificationMapper.toDto(notificationEntity);
                boolean delivered = wsNotifications.notifyUser(notificationDTO);
                if (!delivered) {
                    logger.info("WebSocket delivery failed, cycle notification persisted for userId {}", recipientId);
                }
            }
        } catch (Exception e) {
            logger.error("Error creating/sending new cycle notifications", e);
        }
    }

    /**
     * Creates and sends a new user update notification to the user's manager.
     * <p>
     * The notification content is the user's full name.
     *
     * @param userUpdated the user entity that was updated
     */
    @Transactional
    public void newUserUpdateNotification(UserEntity userUpdated) {
        try {
            if (userUpdated == null) {
                logger.error("Invalid user for user update notification");
                return;
            }
            if( userUpdated.getManager() == null) {
                logger.error("User {} has no manager for user update notification", userUpdated.getId());
                return;
            }
                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setSender(userUpdated);
                notificationEntity.setUser(userUpdated.getManager());
                notificationEntity.setType(NotificationType.USER_UPDATE);
                notificationEntity.setContent(userUpdated.getName() + " " + userUpdated.getSurname());
                notificationEntity.setCreationDate(LocalDateTime.now());
                notificationEntity.setNotificationIsRead(false);
                notificationEntity.setNotificationIsSeen(false);
                notificationEntity.setMessageCount(0);
                notificationRepository.persist(notificationEntity);

                NotificationDTO notificationDTO = notificationMapper.toDto(notificationEntity);
                boolean delivered = wsNotifications.notifyUser(notificationDTO);
                if (!delivered) {
                    logger.info("WebSocket delivery failed, user updated notification persisted for userId {}", userUpdated.getId());
                }
        } catch (Exception e) {
            logger.error("Error creating/sending new cycle notifications", e);
        }
    }
}