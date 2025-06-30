package pt.uc.dei.services;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.dao.MessageDao;
import pt.uc.dei.proj5.dao.NotificationDao;
import pt.uc.dei.proj5.dao.UserDao;
import pt.uc.dei.proj5.dto.MessageDto;
import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.entity.MessageEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class MessageService implements Serializable {
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @Inject
    UserDao userDao;

    @EJB
    MessageDao messageDao;

    @Inject
    NotificationDao notificationDao;

    public List<MessageDto> getMessagesBetween(UserDto user, UserDto otherUser) {
        try {
            List<MessageEntity> messageEntities = messageDao.getListOfMessagesBetween(user.getUsername(), otherUser.getUsername());
            List<MessageDto> messageDtos = messageEntities.stream()
                    .map(this::convertMessageEntityToMessageDto)
                    .collect(Collectors.toList());
            return messageDtos;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public List<UserDto> getAllChats(UserDto user) {
        try {
            return messageDao.getAllConversations(user.getUsername());
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    public boolean readAllConversation(String recipient, String sender) {
        try {
            messageDao.readConversation(recipient, sender);
            return true;
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }

    public MessageDto newMessage(MessageDto messageDto) {
        try {
            MessageEntity newMessageEntity = convertMessageDtoToMessageEntity(messageDto);
            messageDao.persist(newMessageEntity);
            return convertMessageEntityToMessageDto(newMessageEntity);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    private MessageEntity convertMessageDtoToMessageEntity(MessageDto messageDto) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessageId(messageDto.getMessageId());
        messageEntity.setMessage(messageDto.getMessage());
        messageEntity.setRead(messageDto.getRead());
        messageEntity.setDeleted(messageDto.getDeleted());
        messageEntity.setTimestamp(messageDto.getTimestamp());
        messageEntity.setDateEdited(messageEntity.getDateEdited());
        try {
            messageEntity.setSender(userDao.findUserByUsername(messageDto.getSender()));
        } catch (Exception e) {
            logger.error("Error finding sender: " + messageDto.getSender(), e);
            throw new RuntimeException("Failed to convert MessageDto to MessageEntity");
        }
        try {
            messageEntity.setRecipient(userDao.findUserByUsername(messageDto.getRecipient()));
        } catch (Exception e) {
            logger.error("Error finding recipient: " + messageDto.getRecipient(), e);
            throw new RuntimeException("Failed to convert MessageDto to MessageEntity");
        }
        return messageEntity;
    }

    private MessageDto convertMessageEntityToMessageDto(MessageEntity messageEntity) {
        MessageDto messageDto = new MessageDto();
        messageDto.setMessageId(messageEntity.getMessageId());
        messageDto.setMessage(messageEntity.getMessage());
        messageDto.setRead(messageEntity.getRead());
        messageDto.setDeleted(messageEntity.getDeleted());
        messageDto.setTimestamp(messageEntity.getTimestamp());
        messageDto.setSender(messageEntity.getSender().getUsername());
        messageDto.setRecipient(messageEntity.getRecipient().getUsername());
        return messageDto;
    }
}

