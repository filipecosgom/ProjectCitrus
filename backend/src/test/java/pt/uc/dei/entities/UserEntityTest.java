package pt.uc.dei.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    private UserEntity user;
    private UserEntity manager;
    private AppraisalEntity appraisal1;
    private AppraisalEntity appraisal2;
    private FinishedCourseEntity finishedCourse;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        manager = new UserEntity();
        appraisal1 = new AppraisalEntity();
        appraisal2 = new AppraisalEntity();
        finishedCourse = new FinishedCourseEntity();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setHasAvatar(true);
        user.setName("John");
        user.setSurname("Doe");
        user.setUserIsAdmin(true);
        user.setUserIsDeleted(false);
        user.setUserIsManager(true);
        user.setOffice(Office.NO_OFFICE);
        user.setPhone("123456789");
        user.setBirthdate(LocalDate.of(1990, 1, 1));
        user.setStreet("Main St");
        user.setPostalCode("12345");
        user.setMunicipality("City");
        user.setBiography("Bio");
        user.setAccountState(AccountState.COMPLETE);
        user.setRole(Role.CEO);
        user.setCreationDate(LocalDateTime.of(2020, 1, 1, 10, 0));
        user.setSecretKey("secret");
        user.setLastSeen(LocalDateTime.of(2025, 7, 14, 12, 0));
        user.setOnlineStatus(true);
        user.setManagerUser(manager);
        List<AppraisalEntity> received = new ArrayList<>();
        received.add(appraisal1);
        user.setEvaluationsReceived(received);
        List<AppraisalEntity> given = new ArrayList<>();
        given.add(appraisal2);
        user.setEvaluationsGiven(given);
        Set<FinishedCourseEntity> completed = new HashSet<>();
        completed.add(finishedCourse);
        user.setCompletedCourses(completed);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.getHasAvatar());
        assertEquals("John", user.getName());
        assertEquals("Doe", user.getSurname());
        assertTrue(user.getUserIsAdmin());
        assertFalse(user.getUserIsDeleted());
        assertTrue(user.getUserIsManager());
        assertEquals(Office.NO_OFFICE, user.getOffice());
        assertEquals("123456789", user.getPhone());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthdate());
        assertEquals("Main St", user.getStreet());
        assertEquals("12345", user.getPostalCode());
        assertEquals("City", user.getMunicipality());
        assertEquals("Bio", user.getBiography());
        assertEquals(AccountState.COMPLETE, user.getAccountState());
        assertEquals(Role.CEO, user.getRole());
        assertEquals(LocalDateTime.of(2020, 1, 1, 10, 0), user.getCreationDate());
        assertEquals("secret", user.getSecretKey());
        assertEquals(LocalDateTime.of(2025, 7, 14, 12, 0), user.getLastSeen());
        assertTrue(user.getOnlineStatus());
        assertEquals(manager, user.getManagerUser());
        assertEquals(manager, user.getManager());
        assertEquals(1, user.getEvaluationsReceived().size());
        assertEquals(appraisal1, user.getEvaluationsReceived().get(0));
        assertEquals(1, user.getEvaluationsGiven().size());
        assertEquals(appraisal2, user.getEvaluationsGiven().get(0));
        assertEquals(1, user.getCompletedCourses().size());
        assertTrue(user.getCompletedCourses().contains(finishedCourse));
    }

    @Test
    void testSettersUpdateValues() {
        user.setName("Jane");
        assertEquals("Jane", user.getName());
        user.setSurname("Smith");
        assertEquals("Smith", user.getSurname());
        user.setUserIsAdmin(false);
        assertFalse(user.getUserIsAdmin());
        user.setUserIsDeleted(true);
        assertTrue(user.getUserIsDeleted());
        user.setUserIsManager(false);
        assertFalse(user.getUserIsManager());
        user.setOffice(Office.BOSTON);
        assertEquals(Office.BOSTON, user.getOffice());
        user.setPhone("987654321");
        assertEquals("987654321", user.getPhone());
        user.setBirthdate(LocalDate.of(2000, 2, 2));
        assertEquals(LocalDate.of(2000, 2, 2), user.getBirthdate());
        user.setStreet("Second St");
        assertEquals("Second St", user.getStreet());
        user.setPostalCode("54321");
        assertEquals("54321", user.getPostalCode());
        user.setMunicipality("Town");
        assertEquals("Town", user.getMunicipality());
        user.setBiography("New Bio");
        assertEquals("New Bio", user.getBiography());
        user.setAccountState(AccountState.INCOMPLETE);
        assertEquals(AccountState.INCOMPLETE, user.getAccountState());
        user.setRole(Role.CEO);
        assertEquals(Role.CEO, user.getRole());
        user.setOnlineStatus(false);
        assertFalse(user.getOnlineStatus());
    }
}
