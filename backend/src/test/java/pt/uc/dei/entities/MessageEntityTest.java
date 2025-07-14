package pt.uc.dei.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageEntityTest {
    private MessageEntity message;
    private UserEntity sender;
    private UserEntity receiver;

    @BeforeEach
    void setUp() {
        message = new MessageEntity();
        sender = new UserEntity();
        receiver = new UserEntity();
        message.setId(1L);
        message.setSentDate(LocalDateTime.of(2025, 7, 14, 12, 0));
        message.setMessageIsRead(false);
        message.setMessageContent("Hello!");
        message.setSender(sender);
        message.setReceiver(receiver);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, message.getId());
        assertEquals(LocalDateTime.of(2025, 7, 14, 12, 0), message.getSentDate());
        assertFalse(message.getMessageIsRead());
        assertEquals("Hello!", message.getMessageContent());
        assertEquals(sender, message.getSender());
        assertEquals(receiver, message.getReceiver());
    }

    @Test
    void testSettersUpdateValues() {
        message.setMessageIsRead(true);
        assertTrue(message.getMessageIsRead());
        message.setMessageContent("Updated");
        assertEquals("Updated", message.getMessageContent());
        UserEntity newSender = new UserEntity();
        UserEntity newReceiver = new UserEntity();
        message.setSender(newSender);
        message.setReceiver(newReceiver);
        assertEquals(newSender, message.getSender());
        assertEquals(newReceiver, message.getReceiver());
    }
}
