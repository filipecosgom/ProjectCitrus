package pt.uc.dei.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.NotificationType;
import pt.uc.dei.mapper.NotificationMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {
    private NotificationMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = Mappers.getMapper(NotificationMapper.class);
        // Inject mock UserMapper into NotificationMapperImpl
        if (mapper.getClass().getSimpleName().equals("NotificationMapperImpl")) {
            java.lang.reflect.Field userMapperField = mapper.getClass().getDeclaredField("userMapper");
            userMapperField.setAccessible(true);
            userMapperField.set(mapper, new UserMapperMock());
        }
    }

    @Test
    void testToDto() {
        UserEntity sender = new UserEntity();
        sender.setId(1L);
        sender.setEmail("sender@example.com");
        UserEntity recipient = new UserEntity();
        recipient.setId(2L);
        recipient.setEmail("recipient@example.com");
        NotificationEntity entity = new NotificationEntity();
        entity.setId(10L);
        entity.setType(NotificationType.MESSAGE);
        entity.setContent("Test notification");
        entity.setCreationDate(LocalDateTime.of(2025, 7, 14, 12, 0));
        entity.setNotificationIsRead(false);
        entity.setNotificationIsSeen(false);
        entity.setMessageCount(5);
        entity.setUser(recipient);
        entity.setSender(sender);

        NotificationDTO dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getType().toString(), dto.getType());
        assertEquals(entity.getContent(), dto.getContent());
        assertEquals(entity.getCreationDate(), dto.getCreationDate());
        assertEquals(entity.getNotificationIsRead(), dto.getNotificationIsRead());
        assertEquals(entity.getNotificationIsSeen(), dto.getNotificationIsSeen());
        assertEquals(entity.getMessageCount(), dto.getMessageCount());
        assertNotNull(dto.getSender());
        assertNotNull(dto.getRecipient());
        assertEquals(sender.getId(), dto.getSender().getId());
        assertEquals(recipient.getId(), dto.getRecipient().getId());
    }

    @Test
    void testToEntity() {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(20L);
        dto.setType(NotificationType.MESSAGE.name());
        dto.setContent("Entity from DTO");
        dto.setCreationDate(LocalDateTime.of(2025, 7, 14, 13, 0));
        dto.setNotificationIsRead(true);
        dto.setNotificationIsSeen(true);
        dto.setMessageCount(3);
        UserResponseDTO sender = new UserResponseDTO();
        sender.setId(3L);
        UserResponseDTO recipient = new UserResponseDTO();
        recipient.setId(4L);
        dto.setSender(sender);
        dto.setRecipient(recipient);

        NotificationEntity entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getContent(), entity.getContent());
        assertEquals(dto.getCreationDate(), entity.getCreationDate());
        assertEquals(dto.getNotificationIsRead(), entity.getNotificationIsRead());
        assertEquals(dto.getNotificationIsSeen(), entity.getNotificationIsSeen());
        assertEquals(dto.getMessageCount(), entity.getMessageCount());
    }

    @Test
    void testFromMessageDTO() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(30L);
        messageDTO.setSenderId(5L);
        messageDTO.setReceiverId(6L);
        messageDTO.setMessageContent("Message content");
        messageDTO.setSentDate(LocalDateTime.of(2025, 7, 14, 14, 0));
        messageDTO.setMessageIsRead(false);
        int unreadCount = 7;

        NotificationEntity entity = mapper.fromMessageDTO(messageDTO, unreadCount);
        assertNotNull(entity);
        assertEquals(NotificationType.MESSAGE, entity.getType());
        assertEquals(messageDTO.getMessageContent(), entity.getContent());
        assertEquals(unreadCount, entity.getMessageCount());
        assertNotNull(entity.getSender());
        assertEquals(messageDTO.getSenderId(), entity.getSender().getId());
        assertNotNull(entity.getUser());
        assertEquals(messageDTO.getReceiverId(), entity.getUser().getId());
        assertFalse(entity.getNotificationIsRead());
        assertNotNull(entity.getTimestamp());
    }

    @Test
    void testNullMappings() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity((NotificationDTO) null));
    }
}
