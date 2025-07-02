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

import java.io.Serializable;
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


}