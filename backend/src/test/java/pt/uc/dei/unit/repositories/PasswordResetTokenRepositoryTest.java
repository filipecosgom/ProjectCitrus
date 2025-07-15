package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.PasswordResetTokenEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Role;
import pt.uc.dei.enums.Office;
import pt.uc.dei.repositories.PasswordResetTokenRepository;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private PasswordResetTokenRepository repository;
    private UserEntity user;

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
        repository = new PasswordResetTokenRepository();
        // Inject EntityManager via reflection
        try {
            var field = repository.getClass().getSuperclass().getDeclaredField("em");
            field.setAccessible(true);
            field.set(repository, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        em.getTransaction().begin();
        user = createUser("user@example.com", "User");
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
        user.setSecretKey(email + "-secret");
        user.setAccountState(AccountState.COMPLETE);
        user.setRole(Role.SOFTWARE_ENGINEER);
        user.setOnlineStatus(false);
        user.setUserIsAdmin(false);
        user.setUserIsDeleted(false);
        user.setUserIsManager(false);
        user.setOffice(Office.NO_OFFICE);
        em.persist(user);
        return user;
    }

    private PasswordResetTokenEntity createToken(UserEntity user, String value, LocalDateTime creationDate) {
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setUser(user);
        token.setTokenValue(value);
        token.setCreationDate(creationDate);
        em.persist(token);
        return token;
    }

    @Test
    void testGetTokensOfUser_Positive() {
        createToken(user, "token1", LocalDateTime.now());
        em.flush();
        List<PasswordResetTokenEntity> tokens = repository.getTokensOfUser(user);
        assertNotNull(tokens);
        assertFalse(tokens.isEmpty());
        assertEquals(user.getId(), tokens.get(0).getUser().getId());
    }

    @Test
    void testGetTokensOfUser_Negative() {
        UserEntity otherUser = createUser("other@example.com", "Other");
        em.flush();
        List<PasswordResetTokenEntity> tokens = repository.getTokensOfUser(otherUser);
        assertTrue(tokens == null || tokens.isEmpty());
    }

    @Test
    void testGetTokenFromValue_Positive() {
        PasswordResetTokenEntity token = createToken(user, "token2", LocalDateTime.now());
        em.flush();
        PasswordResetTokenEntity found = repository.getTokenFromValue("token2");
        assertNotNull(found);
        assertEquals(token.getTokenValue(), found.getTokenValue());
    }

    @Test
    void testGetTokenFromValue_Negative() {
        PasswordResetTokenEntity found = repository.getTokenFromValue("nonexistent");
        assertNull(found);
    }
}
