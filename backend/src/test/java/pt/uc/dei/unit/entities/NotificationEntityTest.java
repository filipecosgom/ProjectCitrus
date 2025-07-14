package pt.uc.dei.unit.entities;

import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.uc.dei.enums.NotificationType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationEntityTest {
    private NotificationEntity notification;
    private UserEntity user;
    private UserEntity sender;

    @BeforeEach
    void setUp() {
        notification = new NotificationEntity();
        user = new UserEntity();
        sender = new UserEntity();
        notification.setId(1L);
        notification.setType(NotificationType.MESSAGE);
        notification.setContent("Test notification");
        notification.setCreationDate(LocalDateTime.of(2025, 7, 14, 12, 0));
        notification.setNotificationIsRead(false);
        notification.setNotificationIsSeen(false);
        notification.setMessageCount(3);
        notification.setUser(user);
        notification.setSender(sender);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, notification.getId());
        assertEquals(NotificationType.MESSAGE, notification.getType());
        assertEquals("Test notification", notification.getContent());
        assertEquals(LocalDateTime.of(2025, 7, 14, 12, 0), notification.getCreationDate());
        assertFalse(notification.getNotificationIsRead());
        assertFalse(notification.getNotificationIsSeen());
        assertEquals(3, notification.getMessageCount());
        assertEquals(user, notification.getUser());
        assertEquals(sender, notification.getSender());
    }

    @Test
    void testSettersUpdateValues() {
        notification.setContent("Updated");
        assertEquals("Updated", notification.getContent());
        notification.setNotificationIsRead(true);
        assertTrue(notification.getNotificationIsRead());
        notification.setNotificationIsSeen(true);
        assertTrue(notification.getNotificationIsSeen());
        notification.setMessageCount(10);
        assertEquals(10, notification.getMessageCount());
    }

    @Test
    void testConvenienceMethods() {
        notification.setUser(user);
        user.setId(42L);
        assertEquals(42L, notification.getRecipientId());
        notification.setRecipientId(99L);
        assertEquals(99L, notification.getUser().getId());
        notification.setCreationDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        assertEquals(notification.getCreationDate(), notification.getTimestamp());
        notification.setTimestamp(LocalDateTime.of(2026, 2, 2, 11, 0));
        assertEquals(LocalDateTime.of(2026, 2, 2, 11, 0), notification.getCreationDate());
        notification.setMessageCount(7);
        assertEquals(7, notification.getUnreadCount());
        notification.setUnreadCount(8);
        assertEquals(8, notification.getMessageCount());
    }
}
