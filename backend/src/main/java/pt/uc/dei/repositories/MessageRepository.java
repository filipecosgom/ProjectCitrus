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

    public List<Object[]> getAllConversations(Long userId) {
        return em.createNamedQuery("MessageEntity.getAllChats", Object[].class)
                .setParameter("user_id", userId)
                .getResultList();
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

    /**
     * Obtém previews das conversas para o dropdown de mensagens
     * Retorna as últimas conversas do utilizador ordenadas por data
     *
     * @param userId ID do utilizador logado
     * @param limit  Número máximo de conversas a retornar
     * @return Lista de Object[] contendo [UserEntity otherUser, LocalDateTime lastMessageDate]
     */
    public List<Object[]> getConversationPreviews(Long userId, int limit) {
        try {
            List<Object[]> results = em.createNamedQuery("MessageEntity.getConversationPreviews", Object[].class)
                    .setParameter("userId", userId)
                    .setMaxResults(limit) // ✅ LIMIT aplicado na query
                    .getResultList();

            LOGGER.info("Found {} conversation previews for userId {}", results.size(), userId);
            return results;
        } catch (Exception e) {
            LOGGER.error("Error getting conversation previews for userId {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtém a última mensagem entre dois utilizadores
     *
     * @param userId     ID do utilizador logado
     * @param otherUserId ID do outro utilizador
     * @return MessageEntity da última mensagem ou null se não houver
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