package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pt.uc.dei.enums.AppraisalState;

/**
 * Unit tests for {@link UpdateAppraisalDTO}.
 */
class UpdateAppraisalDTOTest {
    @Test
    void testGettersAndSetters() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        Long id = 1L;
        String feedback = "feedback";
        Integer score = 3;
        AppraisalState state = null;
        dto.setId(id);
        dto.setFeedback(feedback);
        dto.setScore(score);
        dto.setState(state);
        assertEquals(id, dto.getId());
        assertEquals(feedback, dto.getFeedback());
        assertEquals(score, dto.getScore());
        assertEquals(state, dto.getState());
    }
}
