package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.ConfigurationEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.ConfigurationRepository;

import jakarta.persistence.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private ConfigurationRepository repository;

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
        repository = new ConfigurationRepository();
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

    private ConfigurationEntity createConfiguration(int loginTime, int verificationTime, int passwordResetTime, boolean twoFactor, UserEntity admin) {
        ConfigurationEntity config = new ConfigurationEntity();
        config.setLoginTime(loginTime);
        config.setVerificationTime(verificationTime);
        config.setPasswordResetTime(passwordResetTime);
        config.setCreationDate(java.time.LocalDateTime.now());
        config.setTwoFactorAuthEnabled(twoFactor);
        config.setAdmin(admin);
        em.persist(config);
        return config;
    }

    @Test
    void testGetLatestConfigurationReturnsMostRecent() {
        UserEntity admin = new UserEntity();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setName("Admin");
        admin.setSurname("User");
        admin.setSecretKey(UUID.randomUUID().toString());
        admin.setAccountState(pt.uc.dei.enums.AccountState.COMPLETE);
        admin.setRole(pt.uc.dei.enums.Role.CEO);
        admin.setOnlineStatus(false);
        em.persist(admin);

        createConfiguration(10, 20, 30, false, admin);
        ConfigurationEntity latest = createConfiguration(15, 25, 35, true, admin);
        em.flush();
        ConfigurationEntity found = repository.getLatestConfiguration();
        assertNotNull(found);
        assertEquals(latest.getLoginTime(), found.getLoginTime());
        assertEquals(latest.getVerificationTime(), found.getVerificationTime());
        assertEquals(latest.getPasswordResetTime(), found.getPasswordResetTime());
        assertEquals(latest.getTwoFactorAuthEnabled(), found.getTwoFactorAuthEnabled());
        assertEquals(latest.getAdmin().getEmail(), found.getAdmin().getEmail());
    }

    @Test
    void testGetLatestConfigurationReturnsNullIfNone() {
        // No configuration persisted
        ConfigurationEntity found = repository.getLatestConfiguration();
        assertNull(found);
    }
}
