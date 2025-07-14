package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.FinishedCourseDTO;
import pt.uc.dei.dtos.UserFullDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
