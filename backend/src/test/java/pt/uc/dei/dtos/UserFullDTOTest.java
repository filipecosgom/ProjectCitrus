package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for {@link UserFullDTO}.
 */
class UserFullDTOTest {
    @Test
    void testGettersAndSetters() {
        UserFullDTO dto = new UserFullDTO();
        List<AppraisalDTO> received = new ArrayList<>();
        List<AppraisalDTO> given = new ArrayList<>();
        Set<FinishedCourseDTO> completed = new HashSet<>();
        dto.setEvaluationsReceived(received);
        dto.setEvaluationsGiven(given);
        dto.setCompletedCourses(completed);
        assertEquals(received, dto.getEvaluationsReceived());
        assertEquals(given, dto.getEvaluationsGiven());
        assertEquals(completed, dto.getCompletedCourses());
    }
}
