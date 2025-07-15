package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.repositories.TemporaryUserRepository;
import jakarta.persistence.*;
import static org.junit.jupiter.api.Assertions.*;

class TemporaryUserRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private TemporaryUserRepository repository;

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
        repository = new TemporaryUserRepository();
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

    private TemporaryUserEntity createTempUser(String email, String password, String secretKey) {
        TemporaryUserEntity user = new TemporaryUserEntity();
        user.setEmail(email);
        user.setPassword(password);
        user.setSecretKey(secretKey);
        em.persist(user);
        return user;
    }

    @Test
    void testFindTemporaryUserByEmail_Positive() {
        TemporaryUserEntity user = createTempUser("tempuser@example.com", "password", "secret");
        em.flush();
        TemporaryUserEntity found = repository.findTemporaryUserByEmail("tempuser@example.com");
        assertNotNull(found);
        assertEquals(user.getEmail(), found.getEmail());
        assertEquals(user.getPassword(), found.getPassword());
        assertEquals(user.getSecretKey(), found.getSecretKey());
    }

    @Test
    void testFindTemporaryUserByEmail_Negative() {
        TemporaryUserEntity found = repository.findTemporaryUserByEmail("nonexistent@example.com");
        assertNull(found);
    }

    @Test
    void testFindTemporaryUserByEmail_NullEmail() {
        TemporaryUserEntity found = repository.findTemporaryUserByEmail(null);
        assertNull(found);
    }

    @Test
    void testFindTemporaryUserByEmail_EmptyEmail() {
        TemporaryUserEntity found = repository.findTemporaryUserByEmail("");
        assertNull(found);
    }

    @Test
    void testFindTemporaryUserByEmail_CaseInsensitive() {
        TemporaryUserEntity user = createTempUser("caseuser@example.com", "password", "secret");
        em.flush();
        TemporaryUserEntity found = repository.findTemporaryUserByEmail("CASEUSER@EXAMPLE.COM");
        // Depending on DB collation, this may or may not work. If not, this test will document the behavior.
        // If case-insensitive search is required, the repository should be updated accordingly.
        // For now, we expect null if the DB is case-sensitive.
        assertNull(found);
    }

    @Test
    void testFindTemporaryUserByEmail_DuplicateEmail() {
        // Should not allow duplicate emails due to unique constraint
        TemporaryUserEntity user1 = createTempUser("dup@example.com", "password1", "secret1");
        em.flush();
        Exception exception = assertThrows(PersistenceException.class, () -> {
            TemporaryUserEntity user2 = new TemporaryUserEntity();
            user2.setEmail("dup@example.com");
            user2.setPassword("password2");
            user2.setSecretKey("secret2");
            em.persist(user2);
            em.flush();
        });
        assertNotNull(exception);
    }
}
