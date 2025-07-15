package pt.uc.dei.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.dtos.NotificationUpdateDTO;
import pt.uc.dei.entities.*;
import pt.uc.dei.enums.NotificationType;
import pt.uc.dei.mapper.NotificationMapper;
import pt.uc.dei.repositories.MessageRepository;
import pt.uc.dei.repositories.NotificationRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.websocket.WsNotifications;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock NotificationRepository notificationRepository;
    @Mock WsNotifications wsNotifications;
    @Mock MessageRepository messageRepository;
    @Mock NotificationMapper notificationMapper;
    @Mock UserRepository userRepository;
    @InjectMocks NotificationService notificationService;

    private MessageDTO messageDTO;
    private NotificationEntity notificationEntity;
    private NotificationDTO notificationDTO;
    private UserEntity sender;
    private UserEntity recipient;
    private AppraisalEntity appraisal;
    private FinishedCourseEntity finishedCourse;
    private CycleEntity cycle;
    private NotificationUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        sender = new UserEntity();
        sender.setId(1L);
        recipient = new UserEntity();
        recipient.setId(2L);
        messageDTO = new MessageDTO();
        messageDTO.setSenderId(1L);
        messageDTO.setRecipientId(2L);
        messageDTO.setContent("Hello");
        notificationEntity = new NotificationEntity();
        notificationEntity.setSender(sender);
        notificationEntity.setUser(recipient);
        notificationEntity.setType(NotificationType.MESSAGE);
        notificationEntity.setContent("Hello");
        notificationEntity.setCreationDate(LocalDateTime.now());
        notificationEntity.setNotificationIsRead(false);
        notificationEntity.setNotificationIsSeen(false);
        notificationEntity.setMessageCount(1);
        notificationDTO = new NotificationDTO();
        updateDTO = new NotificationUpdateDTO();
        updateDTO.setNotificationId(10L);
        updateDTO.setNotificationIsRead(true);
        updateDTO.setNotificationIsSeen(true);
        appraisal = new AppraisalEntity();
        appraisal.setAppraisedUser(recipient);
        appraisal.setAppraisingUser(sender);
        appraisal.setScore(5);
        finishedCourse = mock(FinishedCourseEntity.class);
        recipient = mock(UserEntity.class);
        sender = mock(UserEntity.class);
        cycle = new CycleEntity();
        cycle.setAdmin(sender);
        cycle.setEndDate(java.time.LocalDate.now());
    }

    @Nested
    @DisplayName("newMessageNotification")
    class NewMessageNotification {
        @Test
        void createsAndSendsNotification() throws Exception {
            when(userRepository.findUserById(2L)).thenReturn(recipient);
            when(userRepository.findUserById(1L)).thenReturn(sender);
            when(messageRepository.getUnreadMessageCount(2L, 1L)).thenReturn(1);
            when(notificationRepository.getMessageNotificationBetween(2L, 1L)).thenReturn(null);
            when(notificationMapper.toDto(any())).thenReturn(notificationDTO);
            doReturn(true).when(wsNotifications).notifyUser(notificationDTO);
            assertTrue(notificationService.newMessageNotification(messageDTO));
        }
        @Test
        void returnsFalseIfRecipientNotFound() {
            when(userRepository.findUserById(2L)).thenReturn(null);
            assertFalse(notificationService.newMessageNotification(messageDTO));
        }
        @Test
        void returnsFalseIfSenderNotFound() {
            when(userRepository.findUserById(2L)).thenReturn(recipient);
            when(userRepository.findUserById(1L)).thenReturn(null);
            assertFalse(notificationService.newMessageNotification(messageDTO));
        }
        @Test
        void updatesExistingNotification() throws Exception {
            when(userRepository.findUserById(2L)).thenReturn(recipient);
            when(userRepository.findUserById(1L)).thenReturn(sender);
            when(messageRepository.getUnreadMessageCount(2L, 1L)).thenReturn(2);
            NotificationEntity existing = new NotificationEntity();
            existing.setSender(sender);
            existing.setUser(recipient);
            existing.setType(NotificationType.MESSAGE);
            when(notificationRepository.getMessageNotificationBetween(2L, 1L)).thenReturn(existing);
            when(notificationMapper.toDto(any())).thenReturn(notificationDTO);
            doReturn(false).when(wsNotifications).notifyUser(notificationDTO);
            assertTrue(notificationService.newMessageNotification(messageDTO));
        }
        @Test
        void returnsFalseOnException() {
            when(userRepository.findUserById(anyLong())).thenThrow(new RuntimeException());
            assertFalse(notificationService.newMessageNotification(messageDTO));
        }
    }

    @Nested
    @DisplayName("getNotifications")
    class GetNotifications {
        @Test
        void returnsNotificationDTOs() {
            when(notificationRepository.getNotifications(2L)).thenReturn(List.of(notificationEntity));
            when(notificationMapper.toDto(notificationEntity)).thenReturn(notificationDTO);
            List<NotificationDTO> result = notificationService.getNotifications(2L);
            assertEquals(1, result.size());
            assertEquals(notificationDTO, result.get(0));
        }
        @Test
        void returnsEmptyListOnException() {
            when(notificationRepository.getNotifications(anyLong())).thenThrow(new RuntimeException());
            List<NotificationDTO> result = notificationService.getNotifications(2L);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("updateNotificationStatus")
    class UpdateNotificationStatus {
        @Test
        void updatesStatusSuccessfully() {
            NotificationEntity found = new NotificationEntity();
            found.setUser(recipient);
            when(notificationRepository.findById(10L)).thenReturn(found);
            when(recipient.getId()).thenReturn(2L);
            assertTrue(notificationService.updateNotificationStatus(updateDTO, 2L));
        }
        @Test
        void returnsFalseIfNotificationNotFound() {
            when(notificationRepository.findById(10L)).thenReturn(null);
            assertFalse(notificationService.updateNotificationStatus(updateDTO, 2L));
        }
        @Test
        void returnsFalseIfUserIdMismatch() {
            NotificationEntity found = new NotificationEntity();
            UserEntity wrongUser = new UserEntity();
            wrongUser.setId(99L);
            found.setUser(wrongUser);
            when(notificationRepository.findById(10L)).thenReturn(found);
            assertFalse(notificationService.updateNotificationStatus(updateDTO, 2L));
        }
        @Test
        void returnsFalseOnException() {
            when(notificationRepository.findById(anyLong())).thenThrow(new RuntimeException());
            assertFalse(notificationService.updateNotificationStatus(updateDTO, 2L));
        }
    }

    @Nested
    @DisplayName("getTotalNotifications")
    class GetTotalNotifications {
        @Test
        void returnsTotal() {
            when(notificationRepository.getTotalNotifications(2L)).thenReturn(5);
            assertEquals(5, notificationService.getTotalNotifications(2L));
        }
        @Test
        void returnsZeroOnException() {
            when(notificationRepository.getTotalNotifications(anyLong())).thenThrow(new RuntimeException());
            assertEquals(0, notificationService.getTotalNotifications(2L));
        }
    }

    @Nested
    @DisplayName("markMessageNotificationsAsRead")
    class MarkMessageNotificationsAsRead {
        @Test
        void marksAsRead() {
            when(notificationRepository.markMessageNotificationsAsRead(2L)).thenReturn(true);
            assertTrue(notificationService.markMessageNotificationsAsRead(2L));
        }
        @Test
        void returnsFalseOnException() {
            when(notificationRepository.markMessageNotificationsAsRead(anyLong())).thenThrow(new RuntimeException());
            assertFalse(notificationService.markMessageNotificationsAsRead(2L));
        }
    }

    @Nested
    @DisplayName("newAppraisalNotification")
    class NewAppraisalNotification {
        @Test
        void createsAndSendsAppraisalNotification() throws Exception {
            when(userRepository.findUserById(2L)).thenReturn(recipient);
            when(userRepository.findUserById(1L)).thenReturn(sender);
            when(notificationMapper.toDto(any())).thenReturn(notificationDTO);
            doReturn(true).when(wsNotifications).notifyUser(notificationDTO);
            assertTrue(notificationService.newAppraisalNotification(appraisal));
        }
        @Test
        void returnsFalseIfRecipientNotFound() {
            when(userRepository.findUserById(2L)).thenReturn(null);
            assertFalse(notificationService.newAppraisalNotification(appraisal));
        }
        @Test
        void returnsFalseIfSenderNotFound() {
            when(userRepository.findUserById(2L)).thenReturn(recipient);
            when(userRepository.findUserById(1L)).thenReturn(null);
            assertFalse(notificationService.newAppraisalNotification(appraisal));
        }
        @Test
        void returnsFalseOnException() {
            when(userRepository.findUserById(anyLong())).thenThrow(new RuntimeException());
            assertFalse(notificationService.newAppraisalNotification(appraisal));
        }
    }

    @Nested
    @DisplayName("newCourseNotification")
    class NewCourseNotification {
        @Test
        void createsAndSendsCourseNotification() throws Exception {
            when(finishedCourse.getUser()).thenReturn(recipient);
            when(recipient.getManager()).thenReturn(sender);
            when(userRepository.findUserById(anyLong())).thenReturn(recipient).thenReturn(sender);
            when(notificationMapper.toDto(any())).thenReturn(notificationDTO);
            doReturn(true).when(wsNotifications).notifyUser(notificationDTO);
            assertTrue(notificationService.newCourseNotification(finishedCourse));
        }
        @Test
        void returnsFalseIfRecipientNotFound() {
            when(finishedCourse.getUser()).thenReturn(recipient);
            when(recipient.getManager()).thenReturn(sender);
            when(userRepository.findUserById(anyLong())).thenReturn(null);
            assertFalse(notificationService.newCourseNotification(finishedCourse));
        }
        @Test
        void returnsFalseIfSenderNotFound() {
            when(finishedCourse.getUser()).thenReturn(recipient);
            when(recipient.getManager()).thenReturn(sender);
            when(userRepository.findUserById(anyLong())).thenReturn(recipient).thenReturn(null);
            assertFalse(notificationService.newCourseNotification(finishedCourse));
        }
        @Test
        void returnsFalseOnException() {
            when(finishedCourse.getUser()).thenThrow(new RuntimeException());
            assertFalse(notificationService.newCourseNotification(finishedCourse));
        }
    }

    @Nested
    @DisplayName("newCycleOpenNotification")
    class NewCycleOpenNotification {
        @Test
        void createsNotificationsForAllUsers() throws Exception {
            List<UserEntity> users = Arrays.asList(recipient, sender);
            when(userRepository.findUserById(anyLong())).thenReturn(recipient).thenReturn(sender);
            when(notificationMapper.toDto(any())).thenReturn(notificationDTO);
            doReturn(true).when(wsNotifications).notifyUser(notificationDTO);
            assertDoesNotThrow(() -> notificationService.newCycleOpenNotification(cycle, users));
        }
        @Test
        void handlesNullCycleOrUsers() {
            assertDoesNotThrow(() -> notificationService.newCycleOpenNotification(null, null));
        }
        @Test
        void handlesUserNotFound() {
            List<UserEntity> users = Arrays.asList(recipient);
            when(userRepository.findUserById(anyLong())).thenReturn(null);
            assertDoesNotThrow(() -> notificationService.newCycleOpenNotification(cycle, users));
        }
    }

    @Nested
    @DisplayName("newCycleCloseNotification")
    class NewCycleCloseNotification {
        @Test
        void createsNotificationsForAllUsers() throws Exception {
            List<UserEntity> users = Arrays.asList(recipient, sender);
            when(userRepository.findUserById(anyLong())).thenReturn(recipient).thenReturn(sender);
            when(notificationMapper.toDto(any())).thenReturn(notificationDTO);
            doReturn(true).when(wsNotifications).notifyUser(notificationDTO);
            assertDoesNotThrow(() -> notificationService.newCycleCloseNotification(cycle, users));
        }
        @Test
        void handlesNullCycleOrUsers() {
            assertDoesNotThrow(() -> notificationService.newCycleCloseNotification(null, null));
        }
        @Test
        void handlesUserNotFound() {
            List<UserEntity> users = Arrays.asList(recipient);
            when(userRepository.findUserById(anyLong())).thenReturn(null);
            assertDoesNotThrow(() -> notificationService.newCycleCloseNotification(cycle, users));
        }
    }

    @Nested
    @DisplayName("newUserUpdateNotification")
    class NewUserUpdateNotification {
        @Test
        void createsAndSendsUserUpdateNotification() throws Exception {
            when(notificationMapper.toDto(any())).thenReturn(notificationDTO);
            doReturn(true).when(wsNotifications).notifyUser(notificationDTO);
            assertDoesNotThrow(() -> notificationService.newUserUpdateNotification(recipient));
        }
        @Test
        void handlesNullUser() {
            assertDoesNotThrow(() -> notificationService.newUserUpdateNotification(null));
        }
    }
}
