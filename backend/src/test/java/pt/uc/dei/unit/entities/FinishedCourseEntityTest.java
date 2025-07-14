package pt.uc.dei.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FinishedCourseEntityTest {
    private FinishedCourseEntity finishedCourse;
    private UserEntity user;
    private CourseEntity course;

    @BeforeEach
    void setUp() {
        finishedCourse = new FinishedCourseEntity();
        user = new UserEntity();
        course = new CourseEntity();
        finishedCourse.setId(1L);
        finishedCourse.setUser(user);
        finishedCourse.setCourse(course);
        finishedCourse.setCompletionDate(LocalDate.of(2025, 7, 14));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, finishedCourse.getId());
        assertEquals(user, finishedCourse.getUser());
        assertEquals(course, finishedCourse.getCourse());
        assertEquals(LocalDate.of(2025, 7, 14), finishedCourse.getCompletionDate());
    }

    @Test
    void testSettersUpdateValues() {
        UserEntity newUser = new UserEntity();
        CourseEntity newCourse = new CourseEntity();
        finishedCourse.setUser(newUser);
        finishedCourse.setCourse(newCourse);
        finishedCourse.setCompletionDate(LocalDate.of(2026, 1, 1));
        assertEquals(newUser, finishedCourse.getUser());
        assertEquals(newCourse, finishedCourse.getCourse());
        assertEquals(LocalDate.of(2026, 1, 1), finishedCourse.getCompletionDate());
    }
}
