package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.AssignManagerRequestDTO;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AssignManagerRequestDTO}.
 */
class AssignManagerRequestDTOTest {
    @Test
    void testGettersSettersAndToString() {
        Long managerId = 10L;
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        AssignManagerRequestDTO dto = new AssignManagerRequestDTO();
        dto.setManagerId(managerId);
        dto.setUserIds(userIds);

        assertEquals(managerId, dto.getManagerId());
        assertEquals(userIds, dto.getUserIds());
        assertTrue(dto.toString().contains("managerId=10"));
        assertTrue(dto.toString().contains("userIds=[1, 2, 3]"));
    }

    @Test
    void testAllArgsConstructor() {
        Long managerId = 20L;
        List<Long> userIds = Arrays.asList(4L, 5L);
        AssignManagerRequestDTO dto = new AssignManagerRequestDTO(managerId, userIds);
        assertEquals(managerId, dto.getManagerId());
        assertEquals(userIds, dto.getUserIds());
    }
}
