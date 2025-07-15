 package pt.uc.dei.unit.repositories;

import org.junit.jupiter.api.*;
import pt.uc.dei.entities.MessageEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Role;
import pt.uc.dei.enums.Office;
import pt.uc.dei.repositories.MessageRepository;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MessageRepositoryTest {
    private static EntityManagerFactory emf;
    private EntityManager em;
    private MessageRepository repository;
    private UserEntity userA;
    private UserEntity userB;
    private UserEntity userC;

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
        repository = new MessageRepository();
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
        userB = createUser("userB@example.com", "UserB", Role.PRODUCT_MANAGER);
        userC = createUser("userC@example.com", "UserC", Role.CEO);
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
        user.setUserIsManager(role == Role.PRODUCT_MANAGER);
        user.setOffice(Office.NO_OFFICE);
        em.persist(user);
        return user;
    }

    private MessageEntity createMessage(UserEntity sender, UserEntity receiver, String content, boolean isRead, LocalDateTime sentDate) {
        MessageEntity msg = new MessageEntity();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setMessageContent(content);
        msg.setMessageIsRead(isRead);
        msg.setSentDate(sentDate);
        em.persist(msg);
        return msg;
    }

    @Test
    void testGetListOfMessagesBetween_Positive() {
        createMessage(userA, userB, "Hello B", false, LocalDateTime.now().minusMinutes(10));
        createMessage(userB, userA, "Hi A", true, LocalDateTime.now().minusMinutes(5));
        em.flush();
        List<MessageEntity> messages = repository.getListOfMessagesBetween(userA.getId(), userB.getId());
        assertEquals(2, messages.size());
    }

    @Test
    void testGetListOfMessagesBetween_Negative() {
        List<MessageEntity> messages = repository.getListOfMessagesBetween(userA.getId(), userC.getId());
        assertTrue(messages.isEmpty());
    }

    @Test
    void testGetAllConversations_Positive() {
        createMessage(userA, userB, "Hello B", false, LocalDateTime.now().minusMinutes(10));
        createMessage(userB, userA, "Hi A", true, LocalDateTime.now().minusMinutes(5));
        createMessage(userA, userC, "Hello C", false, LocalDateTime.now().minusMinutes(2));
        em.flush();
        List<Object[]> conversations = repository.getAllConversations(userA.getId());
        assertTrue(conversations.size() >= 2);
    }

    @Test
    void testGetAllConversations_Negative() {
        List<Object[]> conversations = repository.getAllConversations(userC.getId());
        assertTrue(conversations.isEmpty());
    }

    @Test
    void testGetUnreadMessageCount_Positive() {
        createMessage(userA, userB, "Unread 1", false, LocalDateTime.now().minusMinutes(10));
        createMessage(userA, userB, "Unread 2", false, LocalDateTime.now().minusMinutes(5));
        createMessage(userA, userB, "Read", true, LocalDateTime.now().minusMinutes(2));
        em.flush();
        int count = repository.getUnreadMessageCount(userB.getId(), userA.getId());
        assertEquals(2, count);
    }

    @Test
    void testGetUnreadMessageCount_Negative() {
        int count = repository.getUnreadMessageCount(userC.getId(), userA.getId());
        assertEquals(0, count);
    }

    @Test
    void testReadConversation_Positive() {
        createMessage(userA, userB, "Unread 1", false, LocalDateTime.now().minusMinutes(10));
        createMessage(userA, userB, "Unread 2", false, LocalDateTime.now().minusMinutes(5));
        em.flush();
        boolean result = repository.readConversation(userB.getId(), userA.getId());
        assertTrue(result);
        em.flush();
        int count = repository.getUnreadMessageCount(userB.getId(), userA.getId());
        assertEquals(0, count);
    }

    @Test
    void testReadConversation_Negative() {
        boolean result = repository.readConversation(userC.getId(), userA.getId());
        assertTrue(result); // No exception, but nothing to update
    }

    @Test
    void testGetConversationPreviews_Positive() {
        createMessage(userA, userB, "Hello B", false, LocalDateTime.now().minusMinutes(10));
        createMessage(userB, userA, "Hi A", true, LocalDateTime.now().minusMinutes(5));
        createMessage(userA, userC, "Hello C", false, LocalDateTime.now().minusMinutes(2));
        em.flush();
        List<Object[]> previews = repository.getConversationPreviews(userA.getId(), 10);
        assertFalse(previews.isEmpty());
        assertTrue(previews.stream().anyMatch(arr -> arr[0] instanceof UserEntity));
    }

    @Test
    void testGetConversationPreviews_Negative() {
        List<Object[]> previews = repository.getConversationPreviews(userC.getId(), 10);
        assertTrue(previews.isEmpty());
    }

    @Test
    void testGetLastMessageBetween_Positive() {
        createMessage(userA, userB, "First", false, LocalDateTime.now().minusMinutes(10));
        createMessage(userB, userA, "Last", true, LocalDateTime.now().minusMinutes(1));
        em.flush();
        MessageEntity found = repository.getLastMessageBetween(userA.getId(), userB.getId());
        assertNotNull(found);
        assertEquals("Last", found.getMessageContent());
    }

    @Test
    void testGetLastMessageBetween_Negative() {
        MessageEntity found = repository.getLastMessageBetween(userA.getId(), userC.getId());
        assertNull(found);
    }
}
