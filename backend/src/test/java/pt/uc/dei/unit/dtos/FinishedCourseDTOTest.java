package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FinishedCourseDTO}.
 */
class FinishedCourseDTOTest {
    @Test
    void testGettersAndSetters() {
        FinishedCourseDTO dto = new FinishedCourseDTO();
        Long id = 1L;
        Long userId = 2L;
        String userEmail = "user@example.com";
        String courseTitle = "Java";
        String courseDescription = "desc";
        LocalDate completionDate = LocalDate.now();
        Long courseId = 3L;
        String courseArea = "IT";
        Integer courseDuration = 10;
        String courseLanguage = "EN";
        String courseLink = "http://link";
        Boolean courseHasImage = true;
        Boolean courseIsActive = false;
        LocalDate courseCreationDate = LocalDate.now().minusDays(10);

        dto.setId(id);
        dto.setUserId(userId);
        dto.setUserEmail(userEmail);
        dto.setCourseTitle(courseTitle);
        dto.setCourseDescription(courseDescription);
        dto.setCompletionDate(completionDate);
        dto.setCourseId(courseId);
        dto.setCourseArea(courseArea);
        dto.setCourseDuration(courseDuration);
        dto.setCourseLanguage(courseLanguage);
        dto.setCourseLink(courseLink);
        dto.setCourseHasImage(courseHasImage);
        dto.setCourseIsActive(courseIsActive);
        dto.setCourseCreationDate(courseCreationDate);

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(userEmail, dto.getUserEmail());
        assertEquals(courseTitle, dto.getCourseTitle());
        assertEquals(courseDescription, dto.getCourseDescription());
        assertEquals(completionDate, dto.getCompletionDate());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(courseArea, dto.getCourseArea());
        assertEquals(courseDuration, dto.getCourseDuration());
        assertEquals(courseLanguage, dto.getCourseLanguage());
        assertEquals(courseLink, dto.getCourseLink());
        assertEquals(courseHasImage, dto.getCourseHasImage());
        assertEquals(courseIsActive, dto.getCourseIsActive());
        assertEquals(courseCreationDate, dto.getCourseCreationDate());
    }
}
