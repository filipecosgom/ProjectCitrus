package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.*;
import pt.uc.dei.repositories.UserRepository;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private UserRepository repository;

    @BeforeAll
    static void setupClass() {
        emf = Persistence.createEntityManagerFactory("test-unit");
    }

    @AfterAll
    static void tearDownClass() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        repository = new UserRepository();
        // Inject EntityManager via reflection
        try {
            var field = repository.getClass().getSuperclass().getDeclaredField("em");
            field.setAccessible(true);
            field.set(repository, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    private UserEntity createUser(String email, String name, String surname, Role role, Office office, AccountState state, boolean isAdmin, boolean isManager, boolean isDeleted) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("password");
        user.setName(name);
        user.setSurname(surname);
        user.setSecretKey(email + "-secret");
        user.setAccountState(state);
        user.setRole(role);
        user.setOnlineStatus(false);
        user.setUserIsAdmin(isAdmin);
        user.setUserIsDeleted(isDeleted);
        user.setUserIsManager(isManager);
        user.setOffice(office);
        user.setPhone("123456789");
        user.setBirthdate(LocalDate.of(1990, 1, 1));
        user.setStreet("Main St");
        user.setPostalCode("12345");
        user.setMunicipality("TestCity");
        user.setBiography("Test bio");
        user.setCreationDate(LocalDateTime.now());
        em.persist(user);
        return user;
    }

    @Test
    void testFindUserByEmail_Positive() {
        UserEntity user = createUser("user1@example.com", "User1", "Test", Role.SOFTWARE_ENGINEER, Office.LISBON, AccountState.COMPLETE, false, false, false);
        em.flush();
        UserEntity found = repository.findUserByEmail("user1@example.com");
        assertNotNull(found);
        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void testFindUserByEmail_Negative() {
        UserEntity found = repository.findUserByEmail("nonexistent@example.com");
        assertNull(found);
    }

    @Test
    void testFindUserById_Positive() {
        UserEntity user = createUser("user2@example.com", "User2", "Test", Role.CEO, Office.COIMBRA, AccountState.COMPLETE, true, true, false);
        em.flush();
        UserEntity found = repository.findUserById(user.getId());
        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
    }

    @Test
    void testFindUserById_Negative() {
        UserEntity found = repository.findUserById(-1L);
        assertNull(found);
    }

    @Test
    void testGetUsers_Pagination() {
        for (int i = 0; i < 5; i++) {
            createUser("user" + i + "@example.com", "User" + i, "Test", Role.SOFTWARE_ENGINEER, Office.LISBON, AccountState.COMPLETE, false, false, false);
        }
        em.flush();
        List<UserEntity> users = repository.getUsers(null, null, null, null, null, null, null, null, null, null, null, null, 0, 3);
        assertNotNull(users);
        assertTrue(users.size() <= 3);
    }

    @Test
    void testGetTotalUserCount_Positive() {
        createUser("user5@example.com", "User5", "Test", Role.CTO, Office.MUNICH, AccountState.COMPLETE, false, false, false);
        em.flush();
        long count = repository.getTotalUserCount(null, null, null, null, null, null, null, null, null, null);
        assertTrue(count > 0);
    }

    @Test
    void testGetTotalUserCount_Negative() {
        long count = repository.getTotalUserCount(-1L, null, null, null, null, null, null, null, null, null);
        assertEquals(0, count);
    }

    @Test
    void testFindActiveUsersForCycle() {
        createUser("user6@example.com", "User6", "Test", Role.SOFTWARE_ENGINEER, Office.LISBON, AccountState.COMPLETE, false, false, false);
        createUser("user7@example.com", "User7", "Test", Role.SOFTWARE_ENGINEER, Office.LISBON, AccountState.INCOMPLETE, false, false, true);
        em.flush();
        List<UserEntity> users = repository.findActiveUsersForCycle();
        assertNotNull(users);
        assertTrue(users.stream().allMatch(u -> !u.getUserIsDeleted()));
    }

    @Test
    void testCountActiveUsersWithoutManager() {
        createUser("user9@example.com", "User9", "Test", Role.SOFTWARE_ENGINEER, Office.LISBON, AccountState.COMPLETE, false, false, false);
        em.flush();
        long count = repository.countActiveUsersWithoutManager();
        assertTrue(count >= 1);
    }

    @Test
    void testCheckIfUserStillHasManagedUsers_Positive() {
        UserEntity manager = createUser("manager@example.com", "Manager", "Test", Role.CTO, Office.LISBON, AccountState.COMPLETE, false, true, false);
        UserEntity subordinate = createUser("sub@example.com", "Sub", "Test", Role.SOFTWARE_ENGINEER, Office.LISBON, AccountState.COMPLETE, false, false, false);
        subordinate.setManagerUser(manager);
        em.persist(subordinate);
        em.flush();
        boolean hasManaged = repository.checkIfUserStillHasManagedUsers(manager.getId());
        assertTrue(hasManaged);
    }

    @Test
    void testCheckIfUserStillHasManagedUsers_Negative() {
        UserEntity manager = createUser("manager2@example.com", "Manager2", "Test", Role.CTO, Office.LISBON, AccountState.COMPLETE, false, true, false);
        em.flush();
        boolean hasManaged = repository.checkIfUserStillHasManagedUsers(manager.getId());
        assertFalse(hasManaged);
    }

    @Test
    void testFindManagersAndAdmins() {
        createUser("admin@example.com", "Admin", "Test", Role.CEO, Office.LISBON, AccountState.COMPLETE, true, false, false);
        createUser("manager3@example.com", "Manager3", "Test", Role.CTO, Office.LISBON, AccountState.COMPLETE, false, true, false);
        em.flush();
        List<UserEntity> users = repository.findManagersAndAdmins();
        assertNotNull(users);
        assertTrue(users.stream().anyMatch(u -> u.getUserIsAdmin() || u.getUserIsManager()));
    }
}
