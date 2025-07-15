package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Role;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.NotificationType;
import pt.uc.dei.repositories.NotificationRepository;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NotificationRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private NotificationRepository repository;
    private UserEntity userA;
    private UserEntity userB;

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
        repository = new NotificationRepository();
        // Inject EntityManager via reflection
        try {
            var field = repository.getClass().getSuperclass().getDeclaredField("em");
            field.setAccessible(true);
            field.set(repository, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        em.getTransaction().begin();

        userA = createUser("userA@example.com", "UserA", Role.SOFTWARE_ENGINEER);
        userB = createUser("userB@example.com", "UserB", Role.CEO);
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    private UserEntity createUser(String email, String name, Role role) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("password");
        user.setName(name);
        user.setSurname("Test");
        user.setSecretKey(email + "-secret");
        user.setAccountState(AccountState.COMPLETE);
        user.setRole(role);
        user.setOnlineStatus(false);
        user.setUserIsAdmin(role == Role.CEO);
        user.setUserIsDeleted(false);
        user.setUserIsManager(false);
        user.setOffice(Office.NO_OFFICE);
        em.persist(user);
        return user;
    }

    private NotificationEntity createNotification(UserEntity recipient, UserEntity sender, NotificationType type, boolean isRead, boolean isSeen, boolean emailSent, int messageCount, String content, LocalDateTime creationDate) {
        NotificationEntity n = new NotificationEntity();
        n.setUser(recipient);
        n.setSender(sender);
        n.setType(type);
        n.setNotificationIsRead(isRead);
        n.setNotificationIsSeen(isSeen);
        n.setEmailSent(emailSent);
        n.setMessageCount(messageCount);
        n.setContent(content);
        n.setCreationDate(creationDate);
        em.persist(n);
        return n;
    }

    @Test
    void testFindById_Positive() {
        NotificationEntity n = createNotification(userA, userB, NotificationType.MESSAGE, false, false, false, 1, "Test", LocalDateTime.now());
        em.flush();
        NotificationEntity found = repository.findById(n.getId());
        assertNotNull(found);
        assertEquals(n.getId(), found.getId());
    }

    @Test
    void testFindById_Negative() {
        NotificationEntity found = repository.findById(-1L);
        assertNull(found);
    }

    @Test
    void testGetNotifications_Positive() {
        createNotification(userA, userB, NotificationType.MESSAGE, false, false, false, 1, "Test", LocalDateTime.now());
        em.flush();
        List<NotificationEntity> notifications = repository.getNotifications(userA.getId());
        assertFalse(notifications.isEmpty());
    }

    @Test
    void testGetNotifications_Negative() {
        List<NotificationEntity> notifications = repository.getNotifications(-1L);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testGetMessageNotificationBetween_Positive() {
        NotificationEntity n = createNotification(userA, userB, NotificationType.MESSAGE, false, false, false, 1, "Test", LocalDateTime.now());
        em.flush();
        NotificationEntity found = repository.getMessageNotificationBetween(userA.getId(), userB.getId());
        assertNotNull(found);
        assertEquals(n.getId(), found.getId());
    }

    @Test
    void testGetMessageNotificationBetween_Negative() {
        NotificationEntity found = repository.getMessageNotificationBetween(userA.getId(), userB.getId());
        assertNull(found);
    }

    @Test
    void testGetTotalNotifications_Positive() {
        createNotification(userA, userB, NotificationType.MESSAGE, false, false, false, 1, "Test", LocalDateTime.now());
        em.flush();
        int total = repository.getTotalNotifications(userA.getId());
        assertTrue(total > 0);
    }

    @Test
    void testGetTotalNotifications_Negative() {
        int total = repository.getTotalNotifications(-1L);
        assertEquals(0, total);
    }

    @Test
    void testReadNotification_Positive() {
        NotificationEntity n = createNotification(userA, userB, NotificationType.MESSAGE, false, false, false, 1, "Test", LocalDateTime.now());
        em.flush();
        boolean result = repository.readNotification(n.getId(), userA.getId());
        assertTrue(result);
    }

    @Test
    void testReadNotification_Negative() {
        boolean result = repository.readNotification(-1L, userA.getId());
        assertFalse(result);
    }



    @Test
    void testIsNotificationIdValid_Negative() {
        boolean exists = repository.isNotificationIdValid(-1L, userA.getId());
        assertFalse(exists);
    }

    @Test
    void testMarkMessageNotificationsAsRead_Positive() {
        createNotification(userA, userB, NotificationType.MESSAGE, false, false, false, 1, "Test", LocalDateTime.now().minusDays(2));
        em.flush();
        boolean result = repository.markMessageNotificationsAsRead(userA.getId());
        assertTrue(result);
    }

    @Test
    void testMarkMessageNotificationsAsRead_Negative() {
        boolean result = repository.markMessageNotificationsAsRead(-1L);
        assertTrue(result); // No exception, but nothing to update
    }

    @Test
    void testGetUnemailedMessageNotifications_Positive() {
        createNotification(userA, userB, NotificationType.MESSAGE, false, false, false, 1, "Test", LocalDateTime.now().minusDays(2));
        em.flush();
        List<NotificationEntity> notifications = repository.getUnemailedMessageNotifications();
        assertFalse(notifications.isEmpty());
    }

    @Test
    void testGetUnemailedMessageNotifications_Negative() {
        List<NotificationEntity> notifications = repository.getUnemailedMessageNotifications();
        assertTrue(notifications.isEmpty());
    }
}
