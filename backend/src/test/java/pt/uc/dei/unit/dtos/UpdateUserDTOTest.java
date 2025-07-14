package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.UpdateUserDTO;

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
