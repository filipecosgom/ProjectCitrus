package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.repositories.ActivationTokenRepository;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivationTokenRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private ActivationTokenRepository repository;

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
        repository = new ActivationTokenRepository();
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

    @Test
    void testGetIdFromToken() {
        TemporaryUserEntity user = new TemporaryUserEntity();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setSecretKey("secret");
        em.persist(user);

        ActivationTokenEntity token = new ActivationTokenEntity();
        token.setTokenValue("token123");
        token.setCreationDate(LocalDateTime.now());
        token.setTemporaryUser(user);
        em.persist(token);
        em.flush();

        TemporaryUserEntity found = repository.getIdFromToken("token123");
        assertNotNull(found);
        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void testGetTokenFromValue() {
        TemporaryUserEntity user = new TemporaryUserEntity();
        user.setEmail("test2@example.com");
        user.setPassword("password");
        user.setSecretKey("secret");
        em.persist(user);

        ActivationTokenEntity token = new ActivationTokenEntity();
        token.setTokenValue("tokenABC");
        token.setCreationDate(LocalDateTime.now());
        token.setTemporaryUser(user);
        em.persist(token);
        em.flush();

        ActivationTokenEntity found = repository.getTokenFromValue("tokenABC");
        assertNotNull(found);
        assertEquals("tokenABC", found.getTokenValue());
        assertEquals(user.getEmail(), found.getTemporaryUser().getEmail());
    }


    @Test
    void testGetIdFromTokenNotFound() {
        TemporaryUserEntity result = repository.getIdFromToken("notfound");
        assertNull(result);
    }

    @Test
    void testGetTokenFromValueNotFound() {
        ActivationTokenEntity result = repository.getTokenFromValue("notfound");
        assertNull(result);
    }

    @Test
    void testGetTokensOfUserNotFound() {
        TemporaryUserEntity user = new TemporaryUserEntity();
        user.setEmail("nouser@example.com");
        user.setPassword("password");
        user.setSecretKey("secret");
        em.persist(user);
        em.flush();
        List<ActivationTokenEntity> tokens = repository.getTokensOfUser(user);
        assertTrue(tokens == null || tokens.isEmpty());
    }
}
