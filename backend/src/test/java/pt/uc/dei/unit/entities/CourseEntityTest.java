package pt.uc.dei.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Language;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseEntityTest {
    private CourseEntity course;
    private UserEntity admin;

    @BeforeEach
    void setUp() {
        course = new CourseEntity();
        admin = new UserEntity();
        course.setId(1L);
        course.setTitle("Test Course");
        course.setCreationDate(LocalDate.now());
        course.setDuration(40);
        course.setLanguage(Language.ENGLISH);
        course.setArea(CourseArea.FRONTEND);
        course.setDescription("A test course");
        course.setLink("http://test.com");
        course.setCourseHasImage(true);
        course.setCourseIsActive(true);
        course.setAdmin(admin);
        Set<FinishedCourseEntity> completions = new HashSet<>();
        course.setUserCompletions(completions);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, course.getId());
        assertEquals("Test Course", course.getTitle());
        assertNotNull(course.getCreationDate());
        assertEquals(40, course.getDuration());
        assertEquals(Language.ENGLISH, course.getLanguage());
        assertEquals(CourseArea.FRONTEND, course.getArea());
        assertEquals("A test course", course.getDescription());
        assertEquals("http://test.com", course.getLink());
        assertTrue(course.getCourseHasImage());
        assertTrue(course.getCourseIsActive());
        assertEquals(admin, course.getAdmin());
        assertNotNull(course.getUserCompletions());
    }

    @Test
    void testSettersUpdateValues() {
        course.setTitle("New Title");
        assertEquals("New Title", course.getTitle());
        course.setDuration(20);
        assertEquals(20, course.getDuration());
        course.setLanguage(Language.PORTUGUESE);
        assertEquals(Language.PORTUGUESE, course.getLanguage());
        course.setArea(CourseArea.BACKEND);
        assertEquals(CourseArea.BACKEND, course.getArea());
        course.setDescription("Updated");
        assertEquals("Updated", course.getDescription());
        course.setLink("http://new.com");
        assertEquals("http://new.com", course.getLink());
        course.setCourseHasImage(false);
        assertFalse(course.getCourseHasImage());
        course.setCourseIsActive(false);
        assertFalse(course.getCourseIsActive());
    }

    @Test
    void testUserCompletions() {
        Set<FinishedCourseEntity> completions = new HashSet<>();
        FinishedCourseEntity finished = new FinishedCourseEntity();
        completions.add(finished);
        course.setUserCompletions(completions);
        assertEquals(1, course.getUserCompletions().size());
    }
}
