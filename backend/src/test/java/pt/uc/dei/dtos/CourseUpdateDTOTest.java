package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CourseUpdateDTO}.
 */
class CourseUpdateDTOTest {
    @Test
    void testGettersAndSetters() {
        CourseUpdateDTO dto = new CourseUpdateDTO();
        Long id = 1L;
        String title = "Course";
        LocalDate creationDate = LocalDate.now();
        Integer duration = 10;
        Language language = Language.PORTUGUESE;
        CourseArea area = CourseArea.FRONTEND;
        String description = "desc";
        String link = "http://link";
        Boolean courseHasImage = false;
        Boolean courseIsActive = true;

        dto.setId(id);
        dto.setTitle(title);
        dto.setCreationDate(creationDate);
        dto.setDuration(duration);
        dto.setLanguage(language);
        dto.setArea(area);
        dto.setDescription(description);
        dto.setLink(link);
        dto.setCourseHasImage(courseHasImage);
        dto.setCourseIsActive(courseIsActive);

        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(duration, dto.getDuration());
        assertEquals(language, dto.getLanguage());
        assertEquals(area, dto.getArea());
        assertEquals(description, dto.getDescription());
        assertEquals(link, dto.getLink());
        assertEquals(courseHasImage, dto.getCourseHasImage());
        assertEquals(courseIsActive, dto.getCourseIsActive());
    }
}
