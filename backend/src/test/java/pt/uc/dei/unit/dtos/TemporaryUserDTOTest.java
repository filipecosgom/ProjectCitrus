package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TemporaryUserDTO}.
 */
class TemporaryUserDTOTest {
    @Test
    void testGettersAndSetters() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        Long id = 1L;
        String email = "user@example.com";
        String password = "pass";
        String secretKey = "secret";
        dto.setId(id);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setSecretKey(secretKey);
        assertEquals(id, dto.getId());
        assertEquals(email, dto.getEmail());
        assertEquals(password, dto.getPassword());
        assertEquals(secretKey, dto.getSecretKey());
    }
}
