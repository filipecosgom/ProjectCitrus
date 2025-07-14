package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ActivationTokenDTO}.
 */
class ActivationTokenDTOTest {
    @Test
    void testGettersAndSetters() {
        ActivationTokenDTO dto = new ActivationTokenDTO();
        Long id = 123L;
        String tokenValue = "token123";
        LocalDateTime now = LocalDateTime.now();
        TemporaryUserDTO tempUser = new TemporaryUserDTO();

        dto.setId(id);
        dto.setTokenValue(tokenValue);
        dto.setCreationDate(now);
        dto.setTemporaryUser(tempUser);

        assertEquals(id, dto.getId());
        assertEquals(tokenValue, dto.getTokenValue());
        assertEquals(now, dto.getCreationDate());
        assertEquals(tempUser, dto.getTemporaryUser());
    }
}
