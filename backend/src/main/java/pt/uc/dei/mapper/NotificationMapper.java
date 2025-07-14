package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;

import java.time.LocalDateTime;

/**
 * MapStruct mapper interface for converting between {@link NotificationEntity} and {@link NotificationDTO}.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Automatic mapping between entity and DTO, including sender and recipient fields</li>
 *   <li>Custom mapping for user ID to UserEntity for sender and recipient fields</li>
 *   <li>Mapping from MessageDTO to NotificationEntity for message notifications</li>
 *   <li>After-mapping logic to set default values for read status and timestamp</li>
 * </ul>
 * <p>
 * <b>Usage:</b> This interface is implemented automatically by MapStruct at build time.
 * Inject or obtain an instance via CDI or the generated implementation for use in your service layer.
 *
 * @author ProjectCitrus Team
 * @version 1.0
 */
@Mapper(componentModel = "jakarta", uses = UserMapper.class)
public interface NotificationMapper {

    /**
     * Maps a {@link NotificationEntity} to a {@link NotificationDTO}.
     * <p>
     * Maps sender and user (recipient) to their respective DTOs using UserMapper.
     *
     * @param entity the entity to convert
     * @return the mapped DTO, or null if input is null
     */
    @Mapping(target = "sender", source = "sender", qualifiedByName = "toResponseDto")
    @Mapping(target = "recipient", source = "user", qualifiedByName = "toResponseDto")
    @Mapping(source = "notificationIsRead", target = "notificationIsRead")
    @Mapping(source = "notificationIsSeen", target = "notificationIsSeen")
    NotificationDTO toDto(NotificationEntity entity);

    /**
     * Maps a {@link NotificationDTO} to a {@link NotificationEntity}.
     *
     * @param dto the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    NotificationEntity toEntity(NotificationDTO dto);

    /**
     * Creates a {@link NotificationEntity} from a {@link MessageDTO} and unread count.
     * <p>
     * Sets sender and user (recipient) from their IDs, sets type to MESSAGE, and sets default values.
     *
     * @param messageDTO the message DTO to convert
     * @param unreadCount the number of unread messages
     * @return the mapped notification entity
     */
    @Mapping(target = "sender", expression = "java(mapSenderIdToUser(messageDTO.getSenderId()))")
    @Mapping(target = "user", expression = "java(mapRecipientIdToUser(messageDTO.getRecipientId()))")
    @Mapping(target = "type", constant = "MESSAGE")
    @Mapping(target = "content", source = "messageDTO.content")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "notificationIsRead", constant = "false")
    @Mapping(target = "messageCount", source = "unreadCount")
    NotificationEntity fromMessageDTO(MessageDTO messageDTO, int unreadCount);

    /**
     * Helper method to map a recipient user ID to a UserEntity reference.
     *
     * @param recipientId the recipient user ID
     * @return a UserEntity with the given ID, or null if recipientId is null
     */
    default UserEntity mapRecipientIdToUser(Long recipientId) {
        if (recipientId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(recipientId);
        return user;
    }

    /**
     * Helper method to map a sender user ID to a UserEntity reference.
     *
     * @param senderId the sender user ID
     * @return a UserEntity with the given ID, or null if senderId is null
     */
    default UserEntity mapSenderIdToUser(Long senderId) {
        if (senderId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(senderId);
        return user;
    }

    /**
     * After-mapping logic to set default values for read status and timestamp if not set.
     *
     * @param entity the notification entity to update
     */
    @AfterMapping
    default void setDefaultValues(@MappingTarget NotificationEntity entity) {
        if (entity.getNotificationIsRead() == null) {
            entity.setNotificationIsRead(false);
        }
        if (entity.getTimestamp() == null) {
            entity.setTimestamp(LocalDateTime.now());
        }
    }
}
