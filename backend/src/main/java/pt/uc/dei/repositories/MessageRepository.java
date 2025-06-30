package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.dto.*;
import pt.uc.dei.proj5.entity.MessageEntity;

import java.util.Collections;
import java.util.List;

@Stateless
public class MessageDao extends AbstractDao<MessageEntity> {
    private static final Logger logger = LogManager.getLogger(MessageDao.class);
    private static final long serialVersionUID = 1L;

    public MessageDao() {
        super(MessageEntity.class);
    }

    public List<MessageEntity> getListOfMessagesBetween(String userUsername, String otherUserUsername) {
        try {
            List<MessageEntity> messageEntities = em.createNamedQuery("MessageEntity.getConversation", MessageEntity.class)
                    .setParameter("user_username", userUsername)
                    .setParameter("otherUser_username", otherUserUsername)
                    .getResultList();
            return messageEntities;
        }
        catch (Exception e) {
            logger.error("Error fetching conversation: ", e);
            return Collections.emptyList();
        }
    }

    public List<UserDto> getAllConversations(String userUsername) {
        try {
            List<UserDto> usersWithChat = em.createNamedQuery("MessageEntity.getAllChats", UserDto.class)
                    .setParameter("user_username", userUsername)
                    .getResultList();
            return usersWithChat;
        }
        catch (Exception e) {
            logger.error("Error fetching conversation: ", e);
            return Collections.emptyList();
        }
    }

    public int getUnreadMessageCount(String recipientUsername, String senderUsername) {
        try {
            return ((Long) em.createNamedQuery("MessageEntity.getUnreadMessages")
                    .setParameter("recipient_username", recipientUsername)
                    .setParameter("sender_username", senderUsername)
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            logger.error("Error fetching unread messages count: ", e);
            return -1;
        }
    }

    public boolean readConversation(String recipient, String sender) {
        try {
            em.createNamedQuery("MessageEntity.readConversation")
                    .setParameter("recipient_username", recipient)
                    .setParameter("sender_username", sender)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("Error reading conversation", e);
            return false;
        }
    }

    public List<MessageNotificationDto> getMessageNotifications(String recipientUsername) {
        try {
            List<MessageNotificationDto> results = em.createNamedQuery("MessageEntity.getMessageNotifications", MessageNotificationDto.class).setParameter("recipient_username", recipientUsername).getResultList();
            return results;
        }
        catch (Exception e) {
            logger.error(e);
            return Collections.emptyList();
        }
    }


}
