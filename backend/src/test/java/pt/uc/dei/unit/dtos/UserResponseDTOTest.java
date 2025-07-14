package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.FinishedCourseDTO;
import pt.uc.dei.dtos.UserResponseDTO;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for {@link UserResponseDTO}.
 */
class UserResponseDTOTest {
    @Test
    void testGettersAndSetters() {
        UserResponseDTO dto = new UserResponseDTO();
        Set<FinishedCourseDTO> completedCourses = new HashSet<>();
        dto.setCompletedCourses(completedCourses);
        assertEquals(completedCourses, dto.getCompletedCourses());
    }
}
