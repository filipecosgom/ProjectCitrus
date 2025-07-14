package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.NotificationUpdateDTO;

/**
 * Unit tests for {@link NotificationUpdateDTO}.
 */
class NotificationUpdateDTOTest {
    @Test
    void testGettersAndSetters() {
        NotificationUpdateDTO dto = new NotificationUpdateDTO();
        Long notificationId = 1L;
        Boolean notificationIsRead = true;
        Boolean notificationIsSeen = false;
        dto.setNotificationId(notificationId);
        dto.setNotificationIsRead(notificationIsRead);
        dto.setNotificationIsSeen(notificationIsSeen);

        assertEquals(notificationId, dto.getNotificationId());
        assertEquals(notificationIsRead, dto.getNotificationIsRead());
        assertEquals(notificationIsSeen, dto.getNotificationIsSeen());
    }
}
