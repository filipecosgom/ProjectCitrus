package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.ConversationPreviewDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConversationPreviewDTO}.
 */
class ConversationPreviewDTOTest {
    @Test
    void testGettersAndSetters() {
        ConversationPreviewDTO dto = new ConversationPreviewDTO();
        Long userId = 1L;
        String userName = "John";
        String userSurname = "Doe";
        Boolean hasAvatar = true;
        String lastMessage = "Hello";
        LocalDateTime lastMessageDate = LocalDateTime.now();
        Boolean isLastMessageRead = false;
        Integer unreadCount = 2;
        Boolean isLastMessageFromMe = true;

        dto.setUserId(userId);
        dto.setUserName(userName);
        dto.setUserSurname(userSurname);
        dto.setHasAvatar(hasAvatar);
        dto.setLastMessage(lastMessage);
        dto.setLastMessageDate(lastMessageDate);
        dto.setIsLastMessageRead(isLastMessageRead);
        dto.setUnreadCount(unreadCount);
        dto.setIsLastMessageFromMe(isLastMessageFromMe);

        assertEquals(userId, dto.getUserId());
        assertEquals(userName, dto.getUserName());
        assertEquals(userSurname, dto.getUserSurname());
        assertEquals(hasAvatar, dto.getHasAvatar());
        assertEquals(lastMessage, dto.getLastMessage());
        assertEquals(lastMessageDate, dto.getLastMessageDate());
        assertEquals(isLastMessageRead, dto.getIsLastMessageRead());
        assertEquals(unreadCount, dto.getUnreadCount());
        assertEquals(isLastMessageFromMe, dto.getIsLastMessageFromMe());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ConversationPreviewDTO dto = new ConversationPreviewDTO(2L, "Jane", "Smith", false, "Hi", now, true, 5, false);
        assertEquals(2L, dto.getUserId());
        assertEquals("Jane", dto.getUserName());
        assertEquals("Smith", dto.getUserSurname());
        assertFalse(dto.getHasAvatar());
        assertEquals("Hi", dto.getLastMessage());
        assertEquals(now, dto.getLastMessageDate());
        assertTrue(dto.getIsLastMessageRead());
        assertEquals(5, dto.getUnreadCount());
        assertFalse(dto.getIsLastMessageFromMe());
    }
}
