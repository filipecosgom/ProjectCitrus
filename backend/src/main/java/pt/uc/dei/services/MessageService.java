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

/**
 * Service class responsible for handling chat and messaging operations between users.
 * <p>
 * Provides methods for retrieving messages, archiving and delivering new messages, marking conversations as read,
 * fetching chat previews, and integrating with WebSocket and notification systems for real-time updates.
 * <p>
 * All major actions and errors are logged. Most methods are transactional to ensure data consistency.
 */
@Stateless
public class MessageService implements Serializable {
    /**
     * Logger instance for tracking message service events and errors.
     */
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

    /**
     * Retrieves all messages exchanged between two users.
     *
     * @param userId      The ID of the logged-in user.
     * @param otherUserId The ID of the other user in the conversation.
     * @return List of MessageDTOs representing the conversation, or null if an error occurs.
     */
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

    /**
     * Retrieves all chat partners for a given user.
     *
     * @param userId The ID of the logged-in user.
     * @return List of UserResponseDTOs representing users with whom the user has conversations.
     */
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

    /**
     * Marks all messages in a conversation as read for the recipient and notifies the sender via WebSocket.
     *
     * @param recipientId The ID of the user reading the conversation.
     * @param senderId    The ID of the other user in the conversation.
     * @return true if the operation succeeds, false otherwise.
     */
    @Transactional
    public boolean readAllConversation(Long recipientId, Long senderId) {
        try {
            messageRepository.readConversation(recipientId, senderId);
            // Send real-time notification to sender via WebSocket
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
     * Archives a new message and attempts to deliver it via WebSocket. If delivery fails, triggers a notification.
     *
     * @param messageDTO The message to be sent and archived.
     * @return The saved MessageDTO, or null if archiving fails.
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
     * Persists a message entity and returns the saved DTO.
     *
     * @param messageDTO The message to persist.
     * @return The saved MessageDTO, or null if persistence fails.
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
     * Attempts to deliver the message to the recipient via WebSocket.
     * <p>
     * This is currently a stub and always returns false.
     *
     * @param messageDTO The message to deliver.
     * @return true if delivered via WebSocket, false otherwise.
     */
    public boolean sendMessageToUser(MessageDTO messageDTO) {
        // TODO: Implement WebSocket delivery logic
        return false;
    }

    /**
     * Retrieves conversation previews for the message dropdown, combining user data, last message, and unread count.
     *
     * @param userId ID of the logged-in user.
     * @return List of ConversationPreviewDTOs ordered by the date of the last message.
     */
    public List<ConversationPreviewDTO> getConversationPreviews(Long userId) {
        LOGGER.info("Getting conversation previews for userId: {}", userId);
        
        try {
            // 1. Fetch basic conversation data (UserEntity + LastMessageDate)
            List<Object[]> conversationData = messageRepository.getConversationPreviews(userId, 6);
            
            if (conversationData.isEmpty()) {
                LOGGER.info("No conversations found for userId: {}", userId);
                return new ArrayList<>();
            }
            
            // 2. Map each conversation to ConversationPreviewDTO
            List<ConversationPreviewDTO> conversationPreviews = new ArrayList<>();
            
            for (Object[] data : conversationData) {
                try {
                    // Extract data from query
                    UserEntity otherUser = (UserEntity) data[0];
                    LocalDateTime lastMessageDate = (LocalDateTime) data[1];
                    
                    // 3. Fetch the specific last message
                    MessageEntity lastMessage = messageRepository.getLastMessageBetween(userId, otherUser.getId());
                    
                    if (lastMessage == null) {
                        LOGGER.warn("No last message found between userId {} and {}", userId, otherUser.getId());
                        continue; // Skip this conversation
                    }
                    
                    // 4. Fetch unread message count
                    int unreadCount = messageRepository.getUnreadMessageCount(userId, otherUser.getId());
                    
                    // 5. Determine if the last message is from the logged-in user
                    boolean isLastMessageFromMe = lastMessage.getSender().getId().equals(userId);
                    
                    // 6. Determine if the last message is read
                    boolean isLastMessageRead = isLastMessageFromMe ? true : lastMessage.getMessageIsRead();
                    
                    // 7. Create ConversationPreviewDTO
                    ConversationPreviewDTO preview = new ConversationPreviewDTO(
                        otherUser.getId(),
                        otherUser.getName(),
                        otherUser.getSurname(),
                        otherUser.getHasAvatar(),
                        lastMessage.getMessageContent(),
                        lastMessage.getSentDate(),
                        isLastMessageRead,
                        Math.max(0, unreadCount), // Ensure not negative
                        isLastMessageFromMe
                    );
                    
                    conversationPreviews.add(preview);
                    
                } catch (Exception e) {
                    LOGGER.error("Error processing conversation data for userId {}", userId, e);
                    // Continue with next conversation if one fails
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