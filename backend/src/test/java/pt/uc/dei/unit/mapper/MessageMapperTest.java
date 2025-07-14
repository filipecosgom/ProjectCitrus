package pt.uc.dei.unit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.entities.MessageEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.mapper.MessageMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageMapperTest {
    private MessageMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MessageMapper.class);
    }

    @Test
    void testToEntity() {
        MessageDTO dto = new MessageDTO();
        dto.setId(1L);
        dto.setSentDate(LocalDateTime.of(2025, 7, 14, 10, 0));
        dto.setMessageIsRead(false);
        dto.setMessageContent("Hello!");
        dto.setSenderId(100L);
        dto.setReceiverId(200L);

        MessageEntity entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getSentDate(), entity.getSentDate());
        assertEquals(dto.getMessageIsRead(), entity.getMessageIsRead());
        assertEquals(dto.getMessageContent(), entity.getMessageContent());
        assertNotNull(entity.getSender());
        assertEquals(dto.getSenderId(), entity.getSender().getId());
        assertNotNull(entity.getReceiver());
        assertEquals(dto.getReceiverId(), entity.getReceiver().getId());
    }

    @Test
    void testToDto() {
        MessageEntity entity = new MessageEntity();
        entity.setId(2L);
        entity.setSentDate(LocalDateTime.of(2025, 7, 14, 11, 0));
        entity.setMessageIsRead(true);
        entity.setMessageContent("Hi!");
        UserEntity sender = new UserEntity();
        sender.setId(101L);
        UserEntity receiver = new UserEntity();
        receiver.setId(201L);
        entity.setSender(sender);
        entity.setReceiver(receiver);

        MessageDTO dto = mapper.toDto(entity);
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getSentDate(), dto.getSentDate());
        assertEquals(entity.getMessageIsRead(), dto.getMessageIsRead());
        assertEquals(entity.getMessageContent(), dto.getMessageContent());
        assertEquals(sender.getId(), dto.getSenderId());
        assertEquals(receiver.getId(), dto.getReceiverId());
    }

    @Test
    void testNullMappings() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDto(null));
    }
}
