package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.CycleEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Role;
import pt.uc.dei.repositories.CycleRepository;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CycleRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private CycleRepository repository;
    private UserEntity admin;
    private CycleEntity openCycle;
    private CycleEntity closedCycle;

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
        repository = new CycleRepository();
        // Inject EntityManager via reflection
        try {
            var field = repository.getClass().getSuperclass().getDeclaredField("em");
            field.setAccessible(true);
            field.set(repository, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        em.getTransaction().begin();

        admin = new UserEntity();
        admin.setEmail("admin@cycle.com");
        admin.setPassword("password");
        admin.setName("Admin");
        admin.setSurname("Cycle");
        admin.setSecretKey("secret");
        admin.setAccountState(AccountState.COMPLETE);
        admin.setRole(Role.CEO);
        admin.setOnlineStatus(false);
        em.persist(admin);

        openCycle = new CycleEntity();
        openCycle.setState(CycleState.OPEN);
        openCycle.setStartDate(LocalDate.now().minusDays(5));
        openCycle.setEndDate(LocalDate.now().plusDays(5));
        openCycle.setAdmin(admin);
        em.persist(openCycle);

        closedCycle = new CycleEntity();
        closedCycle.setState(CycleState.CLOSED);
        closedCycle.setStartDate(LocalDate.now().minusDays(20));
        closedCycle.setEndDate(LocalDate.now().minusDays(10));
        closedCycle.setAdmin(admin);
        em.persist(closedCycle);
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    @Test
    void testGetAllCycles_Positive() {
        List<CycleEntity> cycles = repository.getAllCycles();
        assertNotNull(cycles);
        assertTrue(cycles.size() >= 2);
    }

    @Test
    void testGetTotalCycles_Positive() {
        long total = repository.getTotalCycles();
        assertTrue(total >= 2);
    }

    @Test
    void testFindCyclesByState_Positive() {
        List<CycleEntity> open = repository.findCyclesByState(CycleState.OPEN);
        assertFalse(open.isEmpty());
        assertEquals(CycleState.OPEN, open.get(0).getState());
    }

    @Test
    void testFindCyclesByState_Negative() {
        List<CycleEntity> none = repository.findCyclesByState(null);
        assertTrue(none.isEmpty());
    }

    @Test
    void testFindCurrentActiveCycle_Positive() {
        CycleEntity current = repository.findCurrentActiveCycle();
        assertNotNull(current);
        assertEquals(CycleState.OPEN, current.getState());
    }

    @Test
    void testFindCurrentActiveCycle_Negative() {
        openCycle.setState(CycleState.CLOSED);
        em.merge(openCycle);
        em.flush();
        CycleEntity current = repository.findCurrentActiveCycle();
        assertNull(current);
    }

    @Test
    void testFindCyclesByAdmin_Positive() {
        List<CycleEntity> cycles = repository.findCyclesByAdmin(admin.getId());
        assertFalse(cycles.isEmpty());
        assertEquals(admin.getId(), cycles.get(0).getAdmin().getId());
    }

    @Test
    void testFindCyclesByAdmin_Negative() {
        List<CycleEntity> cycles = repository.findCyclesByAdmin(-1L);
        assertTrue(cycles.isEmpty());
    }

    @Test
    void testFindCyclesByDateRange_Positive() {
        List<CycleEntity> cycles = repository.findCyclesByDateRange(LocalDate.now().minusDays(30), LocalDate.now());
        assertFalse(cycles.isEmpty());
    }

    @Test
    void testFindCyclesByDateRange_Negative() {
        List<CycleEntity> cycles = repository.findCyclesByDateRange(LocalDate.now().plusDays(100), LocalDate.now().plusDays(200));
        assertTrue(cycles.isEmpty());
    }

    @Test
    void testFindUpcomingCycles_Positive() {
        CycleEntity futureCycle = new CycleEntity();
        futureCycle.setState(CycleState.OPEN);
        futureCycle.setStartDate(LocalDate.now().plusDays(10));
        futureCycle.setEndDate(LocalDate.now().plusDays(20));
        futureCycle.setAdmin(admin);
        em.persist(futureCycle);
        em.flush();
        List<CycleEntity> cycles = repository.findUpcomingCycles();
        assertTrue(cycles.stream().anyMatch(c -> c.getStartDate().equals(futureCycle.getStartDate()) && c.getEndDate().equals(futureCycle.getEndDate())));
    }

    @Test
    void testFindUpcomingCycles_Negative() {
        List<CycleEntity> cycles = repository.findUpcomingCycles();
        assertTrue(cycles.stream().noneMatch(c -> c.getStartDate().isAfter(LocalDate.now().plusDays(100))));
    }

    @Test
    void testFindExpiredOpenCycles_Positive() {
        CycleEntity expiredOpen = new CycleEntity();
        expiredOpen.setState(CycleState.OPEN);
        expiredOpen.setStartDate(LocalDate.now().minusDays(20));
        expiredOpen.setEndDate(LocalDate.now().minusDays(10));
        expiredOpen.setAdmin(admin);
        em.persist(expiredOpen);
        em.flush();
        List<CycleEntity> cycles = repository.findExpiredOpenCycles();
        assertTrue(cycles.stream().anyMatch(c -> c.getStartDate().equals(expiredOpen.getStartDate()) && c.getEndDate().equals(expiredOpen.getEndDate())));
    }

    @Test
    void testFindExpiredOpenCycles_Negative() {
        List<CycleEntity> cycles = repository.findExpiredOpenCycles();
        assertTrue(cycles.stream().noneMatch(c -> c.getEndDate().isAfter(LocalDate.now())));
    }

    @Test
    void testHasOverlappingCycles_Positive() {
        boolean hasOverlap = repository.hasOverlappingCycles(LocalDate.now().minusDays(6), LocalDate.now().plusDays(1), null);
        assertTrue(hasOverlap);
    }

    @Test
    void testHasOverlappingCycles_Negative() {
        boolean hasOverlap = repository.hasOverlappingCycles(LocalDate.now().plusDays(100), LocalDate.now().plusDays(200), null);
        assertFalse(hasOverlap);
    }

    @Test
    void testFindCyclesWithFilters_Positive() {
        List<CycleEntity> cycles = repository.findCyclesWithFilters(CycleState.OPEN, admin.getId(), null, null, null, null);
        assertFalse(cycles.isEmpty());
    }

    @Test
    void testFindCyclesWithFilters_Negative() {
        List<CycleEntity> cycles = repository.findCyclesWithFilters(CycleState.OPEN, -1L, null, null, null, null);
        assertTrue(cycles.isEmpty());
    }

    @Test
    void testCountCyclesWithFilters_Positive() {
        long count = repository.countCyclesWithFilters(CycleState.OPEN, admin.getId(), null, null);
        assertTrue(count > 0);
    }

    @Test
    void testCountCyclesWithFilters_Negative() {
        long count = repository.countCyclesWithFilters(CycleState.OPEN, -1L, null, null);
        assertEquals(0, count);
    }
}
