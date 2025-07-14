package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MessageSendDTO}.
 */
class MessageSendDTOTest {
    @Test
    void testGettersSettersAndAliases() {
        MessageSendDTO dto = new MessageSendDTO();
        String messageContent = "Hello";
        Long receiverId = 1L;
        dto.setMessageContent(messageContent);
        dto.setReceiverId(receiverId);

        assertEquals(messageContent, dto.getMessageContent());
        assertEquals(receiverId, dto.getReceiverId());
        // Aliases
        dto.setRecipientId(2L);
        assertEquals(2L, dto.getReceiverId());
        dto.setContent("Hi");
        assertEquals("Hi", dto.getMessageContent());
    }
}
