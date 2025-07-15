package pt.uc.dei.unit.repositories;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import pt.uc.dei.entities.CourseEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.*;
import pt.uc.dei.repositories.CourseRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link CourseRepository}.
 * <p>
 * Tests positive and negative scenarios for course queries and persistence.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CourseRepositoryTest {

    EntityManager em;
    CourseRepository courseRepository;

    UserEntity admin;
    CourseEntity course1;
    CourseEntity course2;

    @BeforeAll
    void setup() {
        var emf = jakarta.persistence.Persistence.createEntityManagerFactory("test-unit");
        em = emf.createEntityManager();
        em.getTransaction().begin();

        courseRepository = new CourseRepository();
        // Set the EntityManager in the AbstractRepository superclass
        try {
            var emField = courseRepository.getClass().getSuperclass().getDeclaredField("em");
            emField.setAccessible(true);
            emField.set(courseRepository, em);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject EntityManager into CourseRepository", e);
        }

        admin = new UserEntity();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setName("Admin");
        admin.setSurname("User");
        admin.setUserIsAdmin(true);
        admin.setUserIsDeleted(false);
        admin.setUserIsManager(false);
        admin.setOffice(null);
        admin.setAccountState(null);
        admin.setRole(null);
        admin.setSecretKey("secret");
        admin.setOnlineStatus(false);
        admin.setAccountState(AccountState.COMPLETE);
        admin.setOffice(Office.BOSTON);
        admin.setRole(Role.CEO);
        em.persist(admin);

        course1 = new CourseEntity();
        course1.setTitle("Java Basics");
        course1.setCreationDate(LocalDate.now());
        course1.setDuration(10);
        course1.setLanguage(Language.ENGLISH);
        course1.setArea(CourseArea.BACKEND);
        course1.setDescription("Intro to Java");
        course1.setLink("http://java-course");
        course1.setCourseHasImage(false);
        course1.setCourseIsActive(true);
        course1.setAdmin(admin);
        em.persist(course1);

        course2 = new CourseEntity();
        course2.setTitle("Frontend 101");
        course2.setCreationDate(LocalDate.now());
        course2.setDuration(8);
        course2.setLanguage(Language.ENGLISH);
        course2.setArea(CourseArea.FRONTEND);
        course2.setDescription("Intro to Frontend");
        course2.setLink("http://frontend-course");
        course2.setCourseHasImage(true);
        course2.setCourseIsActive(false);
        course2.setAdmin(admin);
        em.persist(course2);
    }

    @AfterAll
    void tearDown() {
        if (em != null) {
            em.getTransaction().commit();
            em.close();
        }
    }

    @Test
    void testGetAllCourses_Positive() {
        List<CourseEntity> courses = courseRepository.getAllCourses();
        assertNotNull(courses);
        assertTrue(courses.size() >= 2);
    }

    @Test
    void testGetTotalCourses_Positive() {
        long total = courseRepository.getTotalCourses();
        assertTrue(total >= 2);
    }

    @Test
    void testFindCoursesByArea_Positive() {
        List<CourseEntity> backendCourses = courseRepository.findCoursesByArea(CourseArea.BACKEND);
        assertFalse(backendCourses.isEmpty());
        assertEquals("Java Basics", backendCourses.get(0).getTitle());
    }

    @Test
    void testFindCoursesByArea_Negative() {
        List<CourseEntity> uxCourses = courseRepository.findCoursesByArea(CourseArea.UX_UI);
        assertTrue(uxCourses.isEmpty());
    }

    @Test
    void testFindCoursesByAdmin_Positive() {
        List<CourseEntity> adminCourses = courseRepository.findCoursesByAdmin(admin.getId());
        assertFalse(adminCourses.isEmpty());
        assertEquals(2, adminCourses.size());
    }

    @Test
    void testFindCoursesByAdmin_Negative() {
        List<CourseEntity> noCourses = courseRepository.findCoursesByAdmin(-1L);
        assertTrue(noCourses.isEmpty());
    }
}
