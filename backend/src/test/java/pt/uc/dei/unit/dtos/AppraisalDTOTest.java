package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.enums.AppraisalState;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link AppraisalDTO}.
 */
class AppraisalDTOTest {
    @Test
    void testGettersAndSetters() {
        AppraisalDTO dto = new AppraisalDTO();
        Long id = 1L;
        Long appraisedUserId = 2L;
        Long appraisingUserId = 3L;
        String feedback = "feedback";
        Long cycleId = 4L;
        Integer score = 5;
        AppraisalState state = null;
        LocalDate creationDate = LocalDate.now();
        LocalDate submissionDate = LocalDate.now();
        UserResponseDTO appraisedUser = null;
        UserResponseDTO appraisingUser = null;

        dto.setId(id);
        dto.setAppraisedUserId(appraisedUserId);
        dto.setAppraisingUserId(appraisingUserId);
        dto.setFeedback(feedback);
        dto.setCycleId(cycleId);
        dto.setScore(score);
        dto.setState(state);
        dto.setCreationDate(creationDate);
        dto.setSubmissionDate(submissionDate);
        dto.setAppraisedUser(appraisedUser);
        dto.setAppraisingUser(appraisingUser);

        assertEquals(id, dto.getId());
        assertEquals(appraisedUserId, dto.getAppraisedUserId());
        assertEquals(appraisingUserId, dto.getAppraisingUserId());
        assertEquals(feedback, dto.getFeedback());
        assertEquals(cycleId, dto.getCycleId());
        assertEquals(score, dto.getScore());
        assertEquals(state, dto.getState());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(submissionDate, dto.getSubmissionDate());
        assertEquals(appraisedUser, dto.getAppraisedUser());
        assertEquals(appraisingUser, dto.getAppraisingUser());
    }
}
