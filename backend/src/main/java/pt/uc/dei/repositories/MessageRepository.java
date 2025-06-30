package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.MessageEntity;

import java.util.Collections;
import java.util.List;

@Stateless
public class MessageRepository extends AbstractRepository<MessageEntity> {
    private static final Logger LOGGER = LogManager.getLogger(MessageRepository.class);
    private static final long serialVersionUID = 1L;

    public MessageRepository() {
        super(MessageEntity.class);
    }

    public List<MessageEntity> getListOfMessagesBetween(Long userId, Long otherUserId) {
        try {
            List<MessageEntity> messageEntities = em.createNamedQuery("MessageEntity.getConversation", MessageEntity.class)
                    .setParameter("user_id", userId)
                    .setParameter("otherUser_id", otherUserId)
                    .getResultList();
            return messageEntities;
        } catch (Exception e) {
            LOGGER.error("Error fetching conversation: ", e);
            return Collections.emptyList();
        }
    }

    public List<Long> getAllConversations(Long userId) {
        try {
            List<Long> usersWithChat = em.createNamedQuery("MessageEntity.getAllChats", Long.class)
                    .setParameter("user_id", userId)
                    .getResultList();
            return usersWithChat;
        } catch (Exception e) {
            LOGGER.error("Error fetching conversation: ", e);
            return Collections.emptyList();
        }
    }

    public int getUnreadMessageCount(Long recipientId, Long senderId) {
        try {
            return ((Long) em.createNamedQuery("MessageEntity.getUnreadMessages")
                    .setParameter("recipient_id", recipientId)
                    .setParameter("sender_id", senderId)
                    .getSingleResult()).intValue();
        } catch (Exception e) {
            LOGGER.error("Error fetching unread messages count: ", e);
            return -1;
        }
    }

    public boolean readConversation(Long recipientId, Long senderId) {
        try {
            em.createNamedQuery("MessageEntity.readConversation")
                    .setParameter("recipient_id", recipientId)
                    .setParameter("sender_id", senderId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            LOGGER.error("Error reading conversation", e);
            return false;
        }
    }
}