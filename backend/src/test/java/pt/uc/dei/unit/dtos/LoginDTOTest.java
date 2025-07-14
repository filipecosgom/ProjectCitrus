package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.LoginDTO;

/**
 * Unit tests for {@link LoginDTO}.
 */
class LoginDTOTest {
    @Test
    void testGettersAndSetters() {
        LoginDTO dto = new LoginDTO();
        String email = "user@example.com";
        String password = "pass";
        String authenticationCode = "123456";

        dto.setEmail(email);
        dto.setPassword(password);
        dto.setAuthenticationCode(authenticationCode);

        assertEquals(email, dto.getEmail());
        assertEquals(password, dto.getPassword());
        assertEquals(authenticationCode, dto.getAuthenticationCode());
    }
}
