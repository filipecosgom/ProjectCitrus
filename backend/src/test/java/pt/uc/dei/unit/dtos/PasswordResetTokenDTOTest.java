package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.PasswordResetTokenDTO;
import pt.uc.dei.dtos.UserDTO;

/**
 * Unit tests for {@link PasswordResetTokenDTO}.
 */
class PasswordResetTokenDTOTest {
    @Test
    void testGettersSettersAndConstructors() {
        PasswordResetTokenDTO dto = new PasswordResetTokenDTO();
        Long id = 1L;
        String tokenValue = "token";
        java.time.LocalDateTime creationDate = java.time.LocalDateTime.now();
        UserDTO user = new UserDTO();
        dto.setId(id);
        dto.setTokenValue(tokenValue);
        dto.setCreationDate(creationDate);
        dto.setUser(user);
        assertEquals(id, dto.getId());
        assertEquals(tokenValue, dto.getTokenValue());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(user, dto.getUser());
    }
    @Test
    void testTokenValueConstructor() {
        PasswordResetTokenDTO dto = new PasswordResetTokenDTO("abc");
        assertEquals("abc", dto.getTokenValue());
    }
}
