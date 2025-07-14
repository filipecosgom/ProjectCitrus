package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.NotificationMessageDTO;

import java.time.LocalDateTime;

/**
 * Unit tests for {@link NotificationMessageDTO}.
 */
class NotificationMessageDTOTest {
    @Test
    void testGettersAndSetters() {
        NotificationMessageDTO dto = new NotificationMessageDTO();
        Long id = 1L;
        String type = "info";
        String content = "content";
        LocalDateTime creationDate = LocalDateTime.now();
        Boolean isRead = true;
        Boolean isSeen = false;
        Integer messageCount = 5;
        Long senderId = 2L;
        Long recipientId = 3L;
        dto.setId(id);
        dto.setType(type);
        dto.setContent(content);
        dto.setCreationDate(creationDate);
        dto.setRead(isRead);
        dto.setSeen(isSeen);
        dto.setMessageCount(messageCount);
        dto.setSenderId(senderId);
        dto.setRecipientId(recipientId);
        assertEquals(id, dto.getId());
        assertEquals(type, dto.getType());
        assertEquals(content, dto.getContent());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(isRead, dto.getRead());
        assertEquals(isSeen, dto.getSeen());
        assertEquals(messageCount, dto.getMessageCount());
        assertEquals(senderId, dto.getSenderId());
        assertEquals(recipientId, dto.getRecipientId());
    }
}
