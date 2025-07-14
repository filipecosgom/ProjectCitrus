package pt.uc.dei.mapper;

import org.mapstruct.*;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.MessageSendDTO;
import pt.uc.dei.entities.MessageEntity;
import pt.uc.dei.entities.UserEntity;

/**
 * MapStruct mapper interface for converting between {@link MessageEntity} and {@link MessageDTO}.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Automatic mapping between entity and DTO, including sender and receiver IDs</li>
 *   <li>Custom mapping for user ID to UserEntity for sender and receiver fields</li>
 * </ul>
 * <p>
 * <b>Usage:</b> This interface is implemented automatically by MapStruct at build time.
 * Inject or obtain an instance via CDI or the generated implementation for use in your service layer.
 *
 * @author ProjectCitrus Team
 * @version 1.0
 */
@Mapper(componentModel = "jakarta")
public interface MessageMapper {

    /**
     * Maps a {@link MessageDTO} to a {@link MessageEntity}.
     * <p>
     * Maps senderId and receiverId to UserEntity references.
     *
     * @param dto the DTO to convert
     * @return the mapped entity, or null if input is null
     */
    @Mapping(target = "receiver", expression = "java(mapUserIdToUserEntity(dto.getReceiverId()))")
    @Mapping(target = "sender", expression = "java(mapUserIdToUserEntity(dto.getSenderId()))")
    MessageEntity toEntity(MessageDTO dto);

    /**
     * Maps a {@link MessageEntity} to a {@link MessageDTO}.
     * <p>
     * Maps sender and receiver UserEntity references to their IDs.
     *
     * @param entity the entity to convert
     * @return the mapped DTO, or null if input is null
     */
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "senderId", source = "sender.id")
    MessageDTO toDto(MessageEntity entity);

    /**
     * Helper method to map a user ID to a UserEntity reference for mapping sender/receiver.
     *
     * @param userId the user ID
     * @return a UserEntity with the given ID, or null if userId is null
     */
    default UserEntity mapUserIdToUserEntity(Long userId) {
        if (userId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(userId);
        return user;
    }
}