package pt.uc.dei.proj5.beans;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.dao.MessageDao;
import pt.uc.dei.proj5.dao.NotificationDao;
import pt.uc.dei.proj5.dao.UserDao;
import pt.uc.dei.proj5.dto.NotificationDto;
import pt.uc.dei.proj5.dto.NotificationType;
import pt.uc.dei.proj5.dto.ProductDto;
import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.entity.NotificationEntity;
import pt.uc.dei.proj5.websocket.wsNotifications;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class NotificationBean {
    private static final Logger logger = LogManager.getLogger(NotificationBean.class);

    @Inject
    NotificationDao notificationDao;

    @Inject
    wsNotifications wsNotifications;

    @Inject
    UserDao userDao;

    @Inject
    MessageDao messageDao;

    public NotificationBean() {
    }

    public List<NotificationDto> getNotifications(UserDto user) {
        try {
            List<NotificationEntity> notificationEntities = notificationDao.getNotifications(user.getUsername());
            List<NotificationDto> notificationDtos = notificationEntities.stream()
                    .map(this::convertNotificationEntityToNotificationDto)
                    .collect(Collectors.toList());
            return notificationDtos;
        } catch (Exception e) {
            logger.error("Failed to get notifications from user " + user.getUsername());
            return Collections.emptyList();
        }
    }

    public int getTotalNotifications(UserDto user) {
        try {
            return notificationDao.getTotalNotifications(user.getUsername());
        } catch (Exception e) {
            logger.error("Failed to get notifications from user " + user.getUsername());
            return 0;
        }
    }

    public boolean readNotification(NotificationDto notificationDto, UserDto recipient) {
        try {
            return notificationDao.readNotification(notificationDto.getId(), recipient.getUsername());
        } catch (Exception e) {
            logger.error("Error reading notification", e);
            return false;
        }
    }

    public boolean newProductNotification(NotificationType type, String senderUsername, String recipientUsername, ProductDto product) {
        try {
            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setType(type);
            notificationEntity.setRecipient(userDao.findUserByUsername(senderUsername));
            notificationEntity.setSender(userDao.findUserByUsername(recipientUsername));
            notificationEntity.setRead(false);
            notificationEntity.setTimestamp(LocalDateTime.now());
            String notificationContent = switch (type) {
                case MESSAGE -> "new message";
                case PRODUCT_BOUGHT -> product.getName();
                case PRODUCT_ALTERED -> product.getName();
            };
            notificationEntity.setContent(notificationContent);
            notificationDao.persist(notificationEntity);
            wsNotifications.notifyUser(convertNotificationEntityToNotificationDto(notificationEntity));
            return true;
        } catch (Exception e) {
            logger.error("Error setting new notification", e);
            return false;
        }
    }

    public boolean newMessageNotification(String message, String senderUsername, String recipientUsername) {
        try {
            int numberUnreadMessages = messageDao.getUnreadMessageCount(recipientUsername, senderUsername);
            if (hasRecipientBeenNotified(senderUsername, recipientUsername)) {
                if(notificationDao.updateMessageNotification(senderUsername, recipientUsername, numberUnreadMessages, message)) {
                    NotificationEntity notification = notificationDao.getChatNotificationBetween(recipientUsername, senderUsername);
                    wsNotifications.notifyUser(convertNotificationEntityToNotificationDto(notification));
                    return true;
                }
                else {
                    return false;
                }
            } else {
                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setType(NotificationType.MESSAGE);
                notificationEntity.setRecipient(userDao.findUserByUsername(recipientUsername));
                notificationEntity.setSender(userDao.findUserByUsername(senderUsername));
                notificationEntity.setRead(false);
                notificationEntity.setTimestamp(LocalDateTime.now());
                notificationEntity.setMessageCount(numberUnreadMessages);
                notificationEntity.setContent(message);
                notificationDao.persist(notificationEntity);
                wsNotifications.notifyUser(convertNotificationEntityToNotificationDto(notificationEntity));
                return true;
            }
        } catch (Exception e) {
            logger.error("Error setting new notification", e);
            return false;
        }
    }


    public boolean checkIfNotificationExists(NotificationDto notificationDto, UserDto recipient) {
        return notificationDao.isNotificationIdValid(notificationDto.getId(), recipient.getUsername());
    }

    private boolean hasRecipientBeenNotified(String senderUsername, String recipientUsername) {
        return notificationDao.hasRecipientBeenNotified(senderUsername, recipientUsername);
    }

    private NotificationDto convertNotificationEntityToNotificationDto(NotificationEntity notificationEntity) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(notificationEntity.getId());
        notificationDto.setType(notificationEntity.getType());
        notificationDto.setContent(notificationEntity.getContent());
        notificationDto.setRead(notificationEntity.isRead());
        notificationDto.setTimestamp(notificationEntity.getTimestamp());
        notificationDto.setMessageCount(notificationEntity.getMessageCount());
        notificationDto.setRecipientUsername(notificationEntity.getRecipient().getUsername());
        notificationDto.setSenderUsername(notificationEntity.getSender().getUsername());
        notificationDto.setSenderProfileUrl(notificationEntity.getSender().getUrl());
        return notificationDto;
    }
}
