package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;

/**
 * Unit tests for {@link CourseDTO}.
 */
class CourseDTOTest {
    @Test
    void testGettersAndSetters() {
        CourseDTO dto = new CourseDTO();
        Long id = 1L;
        String title = "Course";
        LocalDate creationDate = LocalDate.now();
        Integer duration = 10;
        Language language = null;
        CourseArea area = null;
        String description = "desc";
        String link = "http://link";
        Boolean courseHasImage = true;
        Boolean courseIsActive = false;

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
