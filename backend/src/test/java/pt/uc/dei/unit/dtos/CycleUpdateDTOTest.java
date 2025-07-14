package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.enums.CycleState;

import java.time.LocalDate;

/**
 * Unit tests for {@link CycleUpdateDTO}.
 */
class CycleUpdateDTOTest {
    @Test
    void testGettersAndSetters() {
        CycleUpdateDTO dto = new CycleUpdateDTO();
        Long id = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        CycleState state = CycleState.OPEN;
        Long adminId = 2L;

        dto.setId(id);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setState(state);
        dto.setAdminId(adminId);

        assertEquals(id, dto.getId());
        assertEquals(startDate, dto.getStartDate());
        assertEquals(endDate, dto.getEndDate());
        assertEquals(state, dto.getState());
        assertEquals(adminId, dto.getAdminId());
    }
}
