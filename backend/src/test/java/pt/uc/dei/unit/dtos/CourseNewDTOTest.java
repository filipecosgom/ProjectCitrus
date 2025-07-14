package pt.uc.dei.dtos;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CourseNewDTO}.
 */
class CourseNewDTOTest {
    @Test
    void testGettersAndSetters() {
        CourseNewDTO dto = new CourseNewDTO();
        String title = "Course";
        LocalDate creationDate = LocalDate.now();
        Integer duration = 10;
        Language language = Language.ENGLISH;
        CourseArea area = CourseArea.BACKEND;
        String description = "desc";
        String link = "http://link";
        Boolean courseHasImage = true;
        Boolean courseIsActive = false;

        dto.setTitle(title);
        dto.setCreationDate(creationDate);
        dto.setDuration(duration);
        dto.setLanguage(language);
        dto.setArea(area);
        dto.setDescription(description);
        dto.setLink(link);
        dto.setCourseHasImage(courseHasImage);
        dto.setCourseIsActive(courseIsActive);

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
