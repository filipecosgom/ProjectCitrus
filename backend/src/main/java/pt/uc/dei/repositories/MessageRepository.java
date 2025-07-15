package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.MessageEntity;
import pt.uc.dei.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class MessageRepository extends AbstractRepository<MessageEntity> {
    private static final Logger LOGGER = LogManager.getLogger(MessageRepository.class);
    private static final long serialVersionUID = 1L;

    public MessageRepository() {
        super(MessageEntity.class);
    }

    /**
     * Retrieves the list of messages exchanged between two users.
     *
     * @param userId      The ID of the first user
     * @param otherUserId The ID of the second user
     * @return List of MessageEntity representing the conversation, or empty if none
     */
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

    /**
     * Retrieves all conversations for a user.
     *
     * @param userId The ID of the user
     * @return List of Object[] representing all conversations (custom structure)
     */
    public List<Object[]> getAllConversations(Long userId) {
        return em.createNamedQuery("MessageEntity.getAllChats", Object[].class)
                .setParameter("user_id", userId)
                .getResultList();
    }

    /**
     * Gets the count of unread messages for a recipient from a specific sender.
     *
     * @param recipientId The ID of the recipient user
     * @param senderId    The ID of the sender user
     * @return The number of unread messages, or -1 if an error occurs
     */
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

    /**
     * Marks all messages in a conversation as read for a recipient from a sender.
     *
     * @param recipientId The ID of the recipient user
     * @param senderId    The ID of the sender user
     * @return true if the operation succeeded, false otherwise
     */
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

    /**
     * Retrieves conversation previews for the message dropdown.
     * Returns the latest conversations for the user, ordered by date.
     *
     * @param userId The ID of the logged-in user
     * @param limit  The maximum number of conversations to return
     * @return List of Object[] containing [UserEntity otherUser, LocalDateTime lastMessageDate]
     */
    public List<Object[]> getConversationPreviews(Long userId, int limit) {
        try {
            // Usar a query que já funciona (getAllChats)
            List<Object[]> results = em.createNamedQuery("MessageEntity.getAllChats", Object[].class)
                    .setParameter("user_id", userId)
                    .setMaxResults(limit)
                    .getResultList();

            // Converter para o formato esperado: [UserEntity, LocalDateTime]
            List<Object[]> convertedResults = new ArrayList<>();

            for (Object[] result : results) {
                Long otherUserId = (Long) result[0];
                LocalDateTime lastMessageDate = (LocalDateTime) result[1];

                // Buscar o UserEntity
                UserEntity otherUser = em.find(UserEntity.class, otherUserId);
                if (otherUser != null) {
                    convertedResults.add(new Object[]{otherUser, lastMessageDate});
                }
            }

            LOGGER.info("Found {} conversation previews for userId {}", convertedResults.size(), userId);
            return convertedResults;

        } catch (Exception e) {
            LOGGER.error("Error getting conversation previews for userId {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves the last message exchanged between two users.
     *
     * @param userId      The ID of the logged-in user
     * @param otherUserId The ID of the other user
     * @return The last MessageEntity exchanged, or null if none exists
     */
    public MessageEntity getLastMessageBetween(Long userId, Long otherUserId) {
        try {
            List<MessageEntity> messages = em.createNamedQuery("MessageEntity.getConversation", MessageEntity.class)
                    .setParameter("user_id", userId)
                    .setParameter("otherUser_id", otherUserId)
                    .getResultList(); // ✅ Pegar todas as mensagens (sem setMaxResults)

            return messages.isEmpty() ? null : messages.get(messages.size() - 1); // ✅ Última mensagem da lista
        } catch (Exception e) {
            LOGGER.error("Error getting last message between {} and {}", userId, otherUserId, e);
            return null;
        }
    }
}