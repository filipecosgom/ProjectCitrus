package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.ActivationTokenDTO;
import pt.uc.dei.dtos.TemporaryUserDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
