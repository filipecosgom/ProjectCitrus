package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.*;
import pt.uc.dei.enums.*;
import pt.uc.dei.repositories.AppraisalRepository;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AppraisalRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private AppraisalRepository repository;

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
        repository = new AppraisalRepository();
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

    private UserEntity createUser(String email, String name) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("password");
        user.setName(name);
        user.setSurname("Test");
        user.setSecretKey(UUID.randomUUID().toString());
        user.setAccountState(AccountState.COMPLETE);
        user.setRole(Role.CEO);
        user.setOnlineStatus(false);
        em.persist(user);
        return user;
    }

    private CycleEntity createCycle(String name, UserEntity admin, CycleState state) {
        CycleEntity cycle = new CycleEntity();
        cycle.setStartDate(LocalDate.now().minusDays(10));
        cycle.setEndDate(LocalDate.now().plusDays(10));
        cycle.setState(state);
        cycle.setAdmin(admin);
        em.persist(cycle);
        return cycle;
    }

    private AppraisalEntity createAppraisal(UserEntity appraised, UserEntity appraising, CycleEntity cycle, AppraisalState state, int score) {
        AppraisalEntity appraisal = new AppraisalEntity();
        appraisal.setAppraisedUser(appraised);
        appraisal.setAppraisingUser(appraising);
        appraisal.setCycle(cycle);
        appraisal.setState(state);
        appraisal.setScore(score);
        appraisal.setFeedback("Feedback");
        em.persist(appraisal);
        return appraisal;
    }

    @Test
    void testFindAppraisalsByAppraisedUser() {
        UserEntity appraised = createUser("appraised@example.com", "Appraised");
        UserEntity manager = createUser("manager@example.com", "Manager");
        CycleEntity cycle = createCycle("Cycle1", manager, CycleState.OPEN);
        createAppraisal(appraised, manager, cycle, AppraisalState.IN_PROGRESS, 80);
        em.flush();
        List<AppraisalEntity> found = repository.findAppraisalsByAppraisedUser(appraised.getId());
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(appraised.getId(), found.get(0).getAppraisedUser().getId());
    }

    @Test
    void testFindAppraisalsByAppraisingUser() {
        UserEntity appraised = createUser("appraised2@example.com", "Appraised2");
        UserEntity manager = createUser("manager2@example.com", "Manager2");
        CycleEntity cycle = createCycle("Cycle2", manager, CycleState.OPEN);
        createAppraisal(appraised, manager, cycle, AppraisalState.IN_PROGRESS, 90);
        em.flush();
        List<AppraisalEntity> found = repository.findAppraisalsByAppraisingUser(manager.getId());
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(manager.getId(), found.get(0).getAppraisingUser().getId());
    }

    @Test
    void testFindAppraisalsByCycle() {
        UserEntity appraised = createUser("appraised3@example.com", "Appraised3");
        UserEntity manager = createUser("manager3@example.com", "Manager3");
        CycleEntity cycle = createCycle("Cycle3", manager, CycleState.OPEN);
        createAppraisal(appraised, manager, cycle, AppraisalState.IN_PROGRESS, 70);
        em.flush();
        List<AppraisalEntity> found = repository.findAppraisalsByCycle(cycle.getId());
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(cycle.getId(), found.get(0).getCycle().getId());
    }

    @Test
    void testFindAppraisalsByState() {
        UserEntity appraised = createUser("appraised4@example.com", "Appraised4");
        UserEntity manager = createUser("manager4@example.com", "Manager4");
        CycleEntity cycle = createCycle("Cycle4", manager, CycleState.OPEN);
        createAppraisal(appraised, manager, cycle, AppraisalState.COMPLETED, 100);
        em.flush();
        List<AppraisalEntity> found = repository.findAppraisalsByState(AppraisalState.COMPLETED);
        assertNotNull(found);
        assertTrue(found.stream().anyMatch(a -> a.getState() == AppraisalState.COMPLETED));
    }

    @Test
    void testFindAppraisalByUsersAndCycle() {
        UserEntity appraised = createUser("appraised5@example.com", "Appraised5");
        UserEntity manager = createUser("manager5@example.com", "Manager5");
        CycleEntity cycle = createCycle("Cycle5", manager, CycleState.OPEN);
        AppraisalEntity appraisal = createAppraisal(appraised, manager, cycle, AppraisalState.IN_PROGRESS, 60);
        em.flush();
        AppraisalEntity found = repository.findAppraisalByUsersAndCycle(appraised.getId(), manager.getId(), cycle.getId());
        assertNotNull(found);
        assertEquals(appraised.getId(), found.getAppraisedUser().getId());
        assertEquals(manager.getId(), found.getAppraisingUser().getId());
        assertEquals(cycle.getId(), found.getCycle().getId());
    }

    @Test
    void testCountAppraisalsByUser() {
        UserEntity appraised = createUser("appraised6@example.com", "Appraised6");
        UserEntity manager = createUser("manager6@example.com", "Manager6");
        CycleEntity cycle = createCycle("Cycle6", manager, CycleState.OPEN);
        createAppraisal(appraised, manager, cycle, AppraisalState.IN_PROGRESS, 50);
        em.flush();
        Long countAsAppraised = repository.countAppraisalsByUser(appraised.getId(), true);
        Long countAsAppraising = repository.countAppraisalsByUser(manager.getId(), false);
        assertEquals(1L, countAsAppraised);
        assertEquals(1L, countAsAppraising);
    }
}
