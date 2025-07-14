
package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.CreateAppraisalDTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CreateAppraisalDTO}.
 */
class CreateAppraisalDTOTest {
    @Test
    void testGettersAndSetters() {
        CreateAppraisalDTO dto = new CreateAppraisalDTO();
        Long appraisedUserId = 1L;
        Long appraisingUserId = 2L;
        String feedback = "Great job!";
        Long cycleId = 3L;
        Integer score = 4;

        dto.setAppraisedUserId(appraisedUserId);
        dto.setAppraisingUserId(appraisingUserId);
        dto.setFeedback(feedback);
        dto.setCycleId(cycleId);
        dto.setScore(score);

        assertEquals(appraisedUserId, dto.getAppraisedUserId());
        assertEquals(appraisingUserId, dto.getAppraisingUserId());
        assertEquals(feedback, dto.getFeedback());
        assertEquals(cycleId, dto.getCycleId());
        assertEquals(score, dto.getScore());
    }
}
