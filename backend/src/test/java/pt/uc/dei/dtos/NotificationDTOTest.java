package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * Unit tests for {@link NotificationDTO}.
 */
class NotificationDTOTest {
    @Test
    void testGettersAndSetters() {
        NotificationDTO dto = new NotificationDTO();
        Long id = 1L;
        String type = "info";
        String content = "content";
        LocalDateTime creationDate = LocalDateTime.now();
        Boolean notificationIsRead = true;
        Boolean notificationIsSeen = false;
        Integer messageCount = 5;
        dto.setId(id);
        dto.setType(type);
        dto.setContent(content);
        dto.setCreationDate(creationDate);
        dto.setNotificationIsRead(notificationIsRead);
        dto.setNotificationIsSeen(notificationIsSeen);
        dto.setMessageCount(messageCount);
        assertEquals(id, dto.getId());
        assertEquals(type, dto.getType());
        assertEquals(content, dto.getContent());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(notificationIsRead, dto.getNotificationIsRead());
        assertEquals(notificationIsSeen, dto.getNotificationIsSeen());
        assertEquals(messageCount, dto.getMessageCount());
    }
}
