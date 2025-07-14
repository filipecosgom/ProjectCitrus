package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import pt.uc.dei.enums.AppraisalState;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AppraisalResponseDTO}.
 */
class AppraisalResponseDTOTest {
    @Test
    void testGettersAndSetters() {
        AppraisalResponseDTO dto = new AppraisalResponseDTO();
        Long id = 1L;
        String feedback = "feedback";
        Long cycleId = 2L;
        Integer score = 3;
        AppraisalState state = null;
        LocalDate creationDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        LocalDate submissionDate = LocalDate.now();
        UserResponseDTO appraisedUser = null;
        UserResponseDTO appraisingUser = null;

        dto.setId(id);
        dto.setFeedback(feedback);
        dto.setCycleId(cycleId);
        dto.setScore(score);
        dto.setState(state);
        dto.setCreationDate(creationDate);
        dto.setEndDate(endDate);
        dto.setSubmissionDate(submissionDate);
        dto.setAppraisedUser(appraisedUser);
        dto.setAppraisingUser(appraisingUser);

        assertEquals(id, dto.getId());
        assertEquals(feedback, dto.getFeedback());
        assertEquals(cycleId, dto.getCycleId());
        assertEquals(score, dto.getScore());
        assertEquals(state, dto.getState());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(endDate, dto.getEndDate());
        assertEquals(submissionDate, dto.getSubmissionDate());
        assertEquals(appraisedUser, dto.getAppraisedUser());
        assertEquals(appraisingUser, dto.getAppraisingUser());
    }
}
