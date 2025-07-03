package pt.uc.dei.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.MessageRepository;
import pt.uc.dei.repositories.NotificationRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.websocket.WsChat;
import pt.uc.dei.mapper.MessageMapper;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.entities.MessageEntity;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.ConversationPreviewDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
public class MessageService implements Serializable {
    private static final Logger LOGGER = LogManager.getLogger(MessageService.class);


    @Inject
    MessageRepository messageRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    MessageMapper messageMapper;

    @Inject
    UserMapper userMapper;

    @Inject
    NotificationService notificationService;

    @Inject
    WsChat wsChat;

    @Transactional
    public List<MessageDTO> getMessagesBetween(Long userId, Long otherUserId) {
        try {
            return messageRepository.getListOfMessagesBetween(userId, otherUserId)
                    .stream()
                    .map(messageMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    public List<UserResponseDTO> getAllChats(Long userId) {
        try {
            List<Object[]> results = messageRepository.getAllConversations(userId);
            return results.stream()
                    .map(arr -> {
                        Long otherUserId = (Long) arr[0];
                        UserEntity user = userRepository.findUserById(otherUserId);
                        return user != null ? userMapper.toUserResponseDto(user) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Error fetching conversations", e);
            return Collections.emptyList(); // Better than returning null
        }
    }

    @Transactional
    public boolean readAllConversation(Long recipientId, Long senderId) {
        try {
            messageRepository.readConversation(recipientId, senderId);
            // Send real-time notification to sender via WebSocket
            // You may need to adjust how you access wsChat (static, CDI, etc.)
            try {
                JsonObject json = Json.createObjectBuilder()
                    .add("type", "CONVERSATION_READ")
                    .add("readerId", recipientId)
                    .add("senderId", senderId)
                    .build();
                wsChat.sendJsonToUser(json, senderId);
            } catch (Exception ex) {
                LOGGER.warn("Failed to send WebSocket conversation read notification", ex);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
            return false;
        }
    }

    /**
     * Archives and attempts to deliver a message. If WebSocket delivery fails, triggers notification.
     */
    @Transactional
    public MessageDTO newMessage(MessageDTO messageDTO) {
        try {
            // Archive the message
            MessageDTO savedMessage = archiveMessage(messageDTO);
            if (savedMessage == null) {
                LOGGER.error("Failed to archive message from userId {} to userId {}", messageDTO.getSenderId(), messageDTO.getReceiverId());
                return null;
            }
            // Try to deliver via WebSocket
            boolean delivered = sendMessageToUser(savedMessage);
            if (!delivered) {
                LOGGER.info("WebSocket delivery failed, sending notification for message from userId {} to userId {}", messageDTO.getSenderId(), messageDTO.getReceiverId());
                notificationService.newMessageNotification(savedMessage);
            }
            return savedMessage;
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * Persists the message and returns the saved DTO.
     */
    @Transactional
    public MessageDTO archiveMessage(MessageDTO messageDTO) {
        try {
            MessageEntity entity = messageMapper.toEntity(messageDTO);
            messageRepository.persist(entity);
            return messageMapper.toDto(entity);
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * Attempts to deliver the message via WebSocket. (Stub for now)
     */
    public boolean sendMessageToUser(MessageDTO messageDTO) {
        // TODO: Implement WebSocket delivery logic
        return false;
    }

    /**
     * Obtém previews das conversas para o dropdown de mensagens
     * Combina dados de utilizador + última mensagem + contador não lidas
     * @param userId ID do utilizador logado
     * @return Lista de ConversationPreviewDTO ordenada por data da última mensagem
     */
    public List<ConversationPreviewDTO> getConversationPreviews(Long userId) {
        LOGGER.info("Getting conversation previews for userId: {}", userId);
        
        try {
            // 1. Buscar conversas básicas (UserEntity + LastMessageDate)
            List<Object[]> conversationData = messageRepository.getConversationPreviews(userId, 6);
            
            if (conversationData.isEmpty()) {
                LOGGER.info("No conversations found for userId: {}", userId);
                return new ArrayList<>();
            }
            
            // 2. Mapear cada conversa para ConversationPreviewDTO
            List<ConversationPreviewDTO> conversationPreviews = new ArrayList<>();
            
            for (Object[] data : conversationData) {
                try {
                    // Extrair dados da query
                    UserEntity otherUser = (UserEntity) data[0];
                    LocalDateTime lastMessageDate = (LocalDateTime) data[1];
                    
                    // 3. Buscar última mensagem específica
                    MessageEntity lastMessage = messageRepository.getLastMessageBetween(userId, otherUser.getId());
                    
                    if (lastMessage == null) {
                        LOGGER.warn("No last message found between userId {} and {}", userId, otherUser.getId());
                        continue; // Pular esta conversa
                    }
                    
                    // 4. Buscar contador de mensagens não lidas
                    int unreadCount = messageRepository.getUnreadMessageCount(userId, otherUser.getId());
                    
                    // 5. Determinar se a última mensagem é do utilizador logado
                    boolean isLastMessageFromMe = lastMessage.getSender().getId().equals(userId);
                    
                    // 6. Determinar se a última mensagem está lida
                    boolean isLastMessageRead = isLastMessageFromMe ? true : lastMessage.getMessageIsRead();
                    
                    // 7. Criar ConversationPreviewDTO
                    ConversationPreviewDTO preview = new ConversationPreviewDTO(
                        otherUser.getId(),
                        otherUser.getName(),
                        otherUser.getSurname(),
                        otherUser.getHasAvatar(),
                        lastMessage.getMessageContent(),
                        lastMessage.getSentDate(),
                        isLastMessageRead,
                        Math.max(0, unreadCount), // Garantir que não é negativo
                        isLastMessageFromMe
                    );
                    
                    conversationPreviews.add(preview);
                    
                } catch (Exception e) {
                    LOGGER.error("Error processing conversation data for userId {}", userId, e);
                    // Continuar com próxima conversa se uma falhar
                }
            }
            
            LOGGER.info("Successfully created {} conversation previews for userId {}", conversationPreviews.size(), userId);
            return conversationPreviews;
            
        } catch (Exception e) {
            LOGGER.error("Error getting conversation previews for userId {}", userId, e);
            return new ArrayList<>();
        }
    }
}