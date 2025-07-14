package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.ManagerDTO;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link ManagerDTO}.
 */
class ManagerDTOTest {
    @Test
    void testGettersSettersAndConstructors() {
        ManagerDTO dto = new ManagerDTO();
        Long id = 1L;
        String name = "John";
        String surname = "Doe";
        Boolean hasAvatar = true;
        String email = "john@example.com";
        dto.setId(id);
        dto.setName(name);
        dto.setSurname(surname);
        dto.setHasAvatar(hasAvatar);
        dto.setEmail(email);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(surname, dto.getSurname());
        assertEquals(hasAvatar, dto.getHasAvatar());
        assertEquals(email, dto.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        ManagerDTO dto = new ManagerDTO(2L, "Jane", "Smith", null, false, "jane@example.com");
        assertEquals(2L, dto.getId());
        assertEquals("Jane", dto.getName());
        assertEquals("Smith", dto.getSurname());
        assertNull(dto.getRole());
        assertFalse(dto.getHasAvatar());
        assertEquals("jane@example.com", dto.getEmail());
    }
}
