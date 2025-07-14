package pt.uc.dei.entities;

import org.junit.jupiter.api.Test;
import pt.uc.dei.enums.AppraisalState;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AppraisalEntity}.
 */
class AppraisalEntityTest {
    @Test
    void testGettersAndSetters() {
        AppraisalEntity entity = new AppraisalEntity();
        Long id = 1L;
        LocalDate creationDate = LocalDate.of(2024, 1, 1);
        LocalDate editedDate = LocalDate.of(2024, 2, 2);
        LocalDate submissionDate = LocalDate.of(2024, 3, 3);
        Integer score = 5;
        String feedback = "Great job!";
        AppraisalState state = AppraisalState.COMPLETED;
        UserEntity appraisedUser = new UserEntity();
        UserEntity appraisingUser = new UserEntity();
        CycleEntity cycle = new CycleEntity();

        entity.setId(id);
        entity.setCreationDate(creationDate);
        entity.setEditedDate(editedDate);
        entity.setSubmissionDate(submissionDate);
        entity.setScore(score);
        entity.setFeedback(feedback);
        entity.setState(state);
        entity.setAppraisedUser(appraisedUser);
        entity.setAppraisingUser(appraisingUser);
        entity.setCycle(cycle);

        assertEquals(id, entity.getId());
        assertEquals(creationDate, entity.getCreationDate());
        assertEquals(editedDate, entity.getEditedDate());
        assertEquals(submissionDate, entity.getSubmissionDate());
        assertEquals(score, entity.getScore());
        assertEquals(feedback, entity.getFeedback());
        assertEquals(state, entity.getState());
        assertEquals(appraisedUser, entity.getAppraisedUser());
        assertEquals(appraisingUser, entity.getAppraisingUser());
        assertEquals(cycle, entity.getCycle());
    }
}
