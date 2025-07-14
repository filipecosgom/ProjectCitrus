package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UpdateUserDTO}.
 */
class UpdateUserDTOTest {
    @Test
    void testGettersAndSetters() {
        UpdateUserDTO dto = new UpdateUserDTO();
        Long managerId = 1L;
        Boolean hasAvatar = true;
        String name = "John";
        String surname = "Doe";
        dto.setManagerId(managerId);
        dto.setHasAvatar(hasAvatar);
        dto.setName(name);
        dto.setSurname(surname);
        assertEquals(managerId, dto.getManagerId());
        assertEquals(hasAvatar, dto.getHasAvatar());
        assertEquals(name, dto.getName());
        assertEquals(surname, dto.getSurname());
    }
}
