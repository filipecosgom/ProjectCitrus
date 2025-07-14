package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.RequestAuthCodeDTO;

/**
 * Unit tests for {@link RequestAuthCodeDTO}.
 */
class RequestAuthCodeDTOTest {
    @Test
    void testGettersAndSetters() {
        RequestAuthCodeDTO dto = new RequestAuthCodeDTO();
        String email = "user@example.com";
        String password = "pass";
        dto.setEmail(email);
        dto.setPassword(password);
        assertEquals(email, dto.getEmail());
        assertEquals(password, dto.getPassword());
    }
}
