package pt.uc.dei.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NamedQuery(
        name = "MessageEntity.getMessageNotifications",
        query = "SELECT m.sender, COUNT(m), MAX(m.sentDate) " +
                "FROM MessageEntity m " +
                "JOIN UserEntity u ON m.sender.id = u.id " +
                "WHERE m.receiver.id = :recipient_id AND m.isRead = false " +
                "GROUP BY m.sender " +
                "ORDER BY MAX(m.sentDate) DESC"
)

@NamedQuery(
        name = "MessageEntity.getUnreadMessages",
        query = "SELECT COUNT(m) " +
                "FROM MessageEntity m " +
                "WHERE m.receiver.id = :recipient_id " +
                "AND m.sender.id = :sender_id " +
                "AND m.isRead = false"
)

@NamedQuery(
        name = "MessageEntity.getConversation",
        query = "SELECT m " +
                "FROM MessageEntity m " +
                "WHERE (m.receiver.id = :user_id AND m.sender.id = :otherUser_id) " +
                "OR (m.receiver.id = :otherUser_id AND m.sender.id = :user_id) " +
                "ORDER BY m.sentDate ASC"
)

@NamedQuery(
        name = "MessageEntity.getAllChats",
        query = "SELECT DISTINCT u.id " +
                "FROM MessageEntity m " +
                "JOIN UserEntity u ON (u.id = m.sender.id OR u.id = m.receiver.id) " +
                "WHERE (m.receiver.id = :user_id OR m.sender.id = :user_id) " +
                "AND u.id != :user_id " + // Exclude the current user
                "ORDER BY u.id ASC"
)

@NamedQuery(
        name = "MessageEntity.readConversation",
        query = "UPDATE MessageEntity m " +
                "SET m.isRead = true " +
                "WHERE m.receiver.id = :recipient_id AND m.sender.id = :sender_id"
)
@Entity
@Table(name="message", indexes = {
        @Index(name = "idx_recipient_sender_unread", columnList = "recipient, sender, isRead"),
        @Index(name = "idx_conversation_pair", columnList = "recipient, sender, timestamp"),
})
public class MessageEntity implements Serializable {

    /**
     * The unique identifier for the message.
     * Generated automatically.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date and time when the message was sent.
     * Cannot be updated once set.
     */
    @Column(name = "sent_date", nullable = false, unique = false, updatable = false)
    private LocalDateTime sentDate;

    /**
     * Indicates whether the message has been read.
     * Can be updated.
     */
    @Column(name = "is_read", nullable = false, unique = false, updatable = true)
    private Boolean isRead;

    /**
     * The content of the message.
     * Cannot be updated once set.
     */
    @Column(name = "message_content", nullable = false, unique = false, updatable = false)
    private String messageContent;

    /**
     * The user who sent the message.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false, updatable = false)
    private UserEntity sender;

    /**
     * The user who received the message.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false, updatable = false)
    private UserEntity receiver;

    // Getters and Setters

    /**
     * Gets the unique identifier for the message.
     * @return the message ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the message.
     * @param id the message ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the date and time when the message was sent.
     * @return the sent date and time
     */
    public LocalDateTime getSentDate() {
        return sentDate;
    }

    /**
     * Sets the date and time when the message was sent.
     * @param sentDate the sent date and time
     */
    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * Gets whether the message has been read.
     * @return true if read, false otherwise
     */
    public Boolean getRead() {
        return isRead;
    }

    /**
     * Sets whether the message has been read.
     * @param read true if read, false otherwise
     */
    public void setRead(Boolean read) {
        isRead = read;
    }

    /**
     * Gets the content of the message.
     * @return the message content
     */
    public String getMessageContent() {
        return messageContent;
    }

    /**
     * Sets the content of the message.
     * @param messageContent the message content
     */
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    /**
     * Gets the user who sent the message.
     * @return the sender user entity
     */
    public UserEntity getSender() {
        return sender;
    }

    /**
     * Sets the user who sent the message.
     * @param sender the sender user entity
     */
    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    /**
     * Gets the user who received the message.
     * @return the receiver user entity
     */
    public UserEntity getReceiver() {
        return receiver;
    }

    /**
     * Sets the user who received the message.
     * @param receiver the receiver user entity
     */
    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }
}