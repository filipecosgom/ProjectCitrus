package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.AppraisalStatsDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link AppraisalStatsDTO}.
 */
class AppraisalStatsDTOTest {
    @Test
    void testGettersAndSetters() {
        AppraisalStatsDTO dto = new AppraisalStatsDTO();
        Long userId = 1L;
        Long received = 5L;
        Long given = 3L;

        dto.setUserId(userId);
        dto.setReceivedAppraisalsCount(received);
        dto.setGivenAppraisalsCount(given);

        assertEquals(userId, dto.getUserId());
        assertEquals(received, dto.getReceivedAppraisalsCount());
        assertEquals(given, dto.getGivenAppraisalsCount());
    }
}
