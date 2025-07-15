package pt.uc.dei.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.ConversationPreviewDTO;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.MessageEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.MessageRepository;
import pt.uc.dei.repositories.NotificationRepository;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.mapper.MessageMapper;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.websocket.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @Mock MessageRepository messageRepository;
    @Mock NotificationRepository notificationRepository;
    @Mock UserRepository userRepository;
    @Mock MessageMapper messageMapper;
    @Mock UserMapper userMapper;
    @Mock NotificationService notificationService;
    @Mock WsChat wsChat;
    @InjectMocks MessageService messageService;

    private MessageDTO messageDTO;
    private MessageEntity messageEntity;
    private UserEntity sender;
    private UserEntity receiver;
    private List<MessageEntity> messageEntities;
    private List<MessageDTO> messageDTOs;

    @BeforeEach
    void setUp() {
        sender = new UserEntity();
        sender.setId(1L);
        sender.setName("Sender");
        sender.setSurname("User");
        sender.setHasAvatar(true);
        receiver = new UserEntity();
        receiver.setId(2L);
        receiver.setName("Receiver");
        receiver.setSurname("User");
        receiver.setHasAvatar(false);
        messageEntity = new MessageEntity();
        messageEntity.setId(10L);
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setMessageContent("Hello");
        messageEntity.setSentDate(LocalDateTime.now());
        messageEntity.setMessageIsRead(false);
        messageDTO = new MessageDTO();
        messageDTO.setId(10L);
        messageDTO.setSenderId(1L);
        messageDTO.setReceiverId(2L);
        messageDTO.setMessageContent("Hello");
        messageDTO.setSentDate(LocalDateTime.now());
        messageDTO.setMessageIsRead(false);
        messageEntities = List.of(messageEntity);
        messageDTOs = List.of(messageDTO);
    }

    @Nested
    @DisplayName("getMessagesBetween")
    class GetMessagesBetween {
        @Test
        void returnsMessagesBetweenUsers() {
            when(messageRepository.getListOfMessagesBetween(1L, 2L)).thenReturn(messageEntities);
            when(messageMapper.toDto(messageEntity)).thenReturn(messageDTO);
            List<MessageDTO> result = messageService.getMessagesBetween(1L, 2L);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(messageDTO, result.get(0));
        }
        @Test
        void returnsNullOnException() {
            when(messageRepository.getListOfMessagesBetween(anyLong(), anyLong())).thenThrow(new RuntimeException());
            assertNull(messageService.getMessagesBetween(1L, 2L));
        }
    }

    @Nested
    @DisplayName("getAllChats")
    class GetAllChats {
        @Test
        void returnsUserResponseDTOs() {
            Object[] arr = new Object[]{2L};
            when(messageRepository.getAllConversations(1L)).thenReturn(Collections.singletonList(arr));
            when(userRepository.findUserById(2L)).thenReturn(receiver);
            UserResponseDTO userResponseDTO = mock(UserResponseDTO.class);
            when(userMapper.toUserResponseDto(receiver)).thenReturn(userResponseDTO);
            List<UserResponseDTO> result = messageService.getAllChats(1L);
            assertEquals(1, result.size());
            assertEquals(userResponseDTO, result.get(0));
        }
        @Test
        void skipsNullUsers() {
            Object[] arr = new Object[]{3L};
            when(messageRepository.getAllConversations(1L)).thenReturn(Collections.singletonList(arr));
            when(userRepository.findUserById(3L)).thenReturn(null);
            List<UserResponseDTO> result = messageService.getAllChats(1L);
            assertTrue(result.isEmpty());
        }
        @Test
        void returnsEmptyListOnException() {
            when(messageRepository.getAllConversations(anyLong())).thenThrow(new RuntimeException());
            List<UserResponseDTO> result = messageService.getAllChats(1L);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("readAllConversation")
    class ReadAllConversation {

        @Test
        void logsAndReturnsFalseOnException() {
            when(messageRepository.readConversation(anyLong(), anyLong())).thenThrow(new RuntimeException());
            assertFalse(messageService.readAllConversation(2L, 1L));
        }
    }

    @Nested
    @DisplayName("newMessage")
    class NewMessage {
        @Test
        void archivesAndDeliversMessage() {
            MessageDTO saved = new MessageDTO();
            saved.setSenderId(1L);
            saved.setReceiverId(2L);
            MessageService spy = Mockito.spy(messageService);
            doReturn(saved).when(spy).archiveMessage(messageDTO);
            doReturn(true).when(spy).sendMessageToUser(saved);
            MessageDTO result = spy.newMessage(messageDTO);
            assertEquals(saved, result);
        }
        @Test
        void returnsNullIfArchiveFails() {
            MessageService spy = Mockito.spy(messageService);
            doReturn(null).when(spy).archiveMessage(messageDTO);
            MessageDTO result = spy.newMessage(messageDTO);
            assertNull(result);
        }
        @Test
        void returnsNullOnException() {
            MessageService spy = Mockito.spy(messageService);
            doThrow(new RuntimeException()).when(spy).archiveMessage(messageDTO);
            MessageDTO result = spy.newMessage(messageDTO);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("archiveMessage")
    class ArchiveMessage {
        @Test
        void persistsAndReturnsDTO() {
            when(messageMapper.toEntity(messageDTO)).thenReturn(messageEntity);
            doNothing().when(messageRepository).persist(messageEntity);
            when(messageMapper.toDto(messageEntity)).thenReturn(messageDTO);
            MessageDTO result = messageService.archiveMessage(messageDTO);
            assertEquals(messageDTO, result);
        }
        @Test
        void returnsNullOnException() {
            when(messageMapper.toEntity(any())).thenThrow(new RuntimeException());
            assertNull(messageService.archiveMessage(messageDTO));
        }
    }

    @Nested
    @DisplayName("sendMessageToUser")
    class SendMessageToUser {
        @Test
        void returnsFalseByDefault() {
            assertFalse(messageService.sendMessageToUser(messageDTO));
        }
    }

    @Nested
    @DisplayName("getConversationPreviews")
    class GetConversationPreviews {
        @Test
        void returnsConversationPreviews() {
            Object[] data = new Object[]{receiver, LocalDateTime.now()};
            when(messageRepository.getConversationPreviews(1L, 6)).thenReturn(Collections.singletonList(data));
            MessageEntity lastMessage = new MessageEntity();
            lastMessage.setSender(sender);
            lastMessage.setReceiver(receiver);
            lastMessage.setMessageContent("Hi");
            lastMessage.setSentDate(LocalDateTime.now());
            lastMessage.setMessageIsRead(true);
            when(messageRepository.getLastMessageBetween(1L, 2L)).thenReturn(lastMessage);
            when(messageRepository.getUnreadMessageCount(1L, 2L)).thenReturn(0);
            List<ConversationPreviewDTO> result = messageService.getConversationPreviews(1L);
            assertEquals(1, result.size());
            ConversationPreviewDTO preview = result.get(0);
            assertEquals(receiver.getId(), preview.getUserId());
            assertEquals("Hi", preview.getLastMessage());
            assertTrue(preview.getIsLastMessageRead());
        }
        @Test
        void skipsConversationIfNoLastMessage() {
            Object[] data = new Object[]{receiver, LocalDateTime.now()};
            when(messageRepository.getConversationPreviews(1L, 6)).thenReturn(Collections.singletonList(data));
            when(messageRepository.getLastMessageBetween(1L, 2L)).thenReturn(null);
            List<ConversationPreviewDTO> result = messageService.getConversationPreviews(1L);
            assertTrue(result.isEmpty());
        }
        @Test
        void returnsEmptyListIfNoConversations() {
            when(messageRepository.getConversationPreviews(1L, 6)).thenReturn(Collections.emptyList());
            List<ConversationPreviewDTO> result = messageService.getConversationPreviews(1L);
            assertTrue(result.isEmpty());
        }
        @Test
        void returnsEmptyListOnException() {
            when(messageRepository.getConversationPreviews(anyLong(), anyInt())).thenThrow(new RuntimeException());
            List<ConversationPreviewDTO> result = messageService.getConversationPreviews(1L);
            assertTrue(result.isEmpty());
        }
    }
}
