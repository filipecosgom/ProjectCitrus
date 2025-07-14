package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.enums.CycleState;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for {@link CycleDTO}.
 */
class CycleDTOTest {
    @Test
    void testGettersAndSetters() {
        CycleDTO dto = new CycleDTO();
        Long id = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        CycleState state = CycleState.OPEN;
        Long adminId = 2L;
        List<AppraisalDTO> evaluations = Arrays.asList(new AppraisalDTO(), new AppraisalDTO());

        dto.setId(id);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setState(state);
        dto.setAdminId(adminId);
        dto.setEvaluations(evaluations);

        assertEquals(id, dto.getId());
        assertEquals(startDate, dto.getStartDate());
        assertEquals(endDate, dto.getEndDate());
        assertEquals(state, dto.getState());
        assertEquals(adminId, dto.getAdminId());
        assertEquals(evaluations, dto.getEvaluations());
    }
}
