package pt.uc.dei.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NamedQuery(
        name = "MessageEntity.getMessageNotifications",
        query = "SELECT m.sender, COUNT(m), MAX(m.sentDate) " +
                "FROM MessageEntity m " +
                "JOIN UserEntity u ON m.sender.id = u.id " +
                "WHERE m.receiver.id = :recipient_id AND m.messageIsRead = false " +
                "GROUP BY m.sender " +
                "ORDER BY MAX(m.sentDate) DESC"
)

@NamedQuery(
        name = "MessageEntity.getUnreadMessages",
        query = "SELECT COUNT(m) " +
                "FROM MessageEntity m " +
                "WHERE m.receiver.id = :recipient_id " +
                "AND m.sender.id = :sender_id " +
                "AND m.messageIsRead = false"
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
        query = "SELECT u.id, MAX(m.sentDate) as lastMessageDate " +
                "FROM MessageEntity m " +
                "JOIN UserEntity u ON (u.id = m.sender.id OR u.id = m.receiver.id) " +
                "WHERE (m.receiver.id = :user_id OR m.sender.id = :user_id) " +
                "AND u.id != :user_id " +
                "GROUP BY u.id " +
                "ORDER BY lastMessageDate DESC"
)

@NamedQuery(
        name = "MessageEntity.readConversation",
        query = "UPDATE MessageEntity m " +
                "SET m.messageIsRead = true " +
                "WHERE m.receiver.id = :recipient_id AND m.sender.id = :sender_id"
)

// SUBSTITUIR a NamedQuery existente por:

@NamedQuery(
    name = "MessageEntity.getConversationPreviews",
    query = "SELECT m.sender.id, m.receiver.id, MAX(m.sentDate) as lastMessageDate " +
            "FROM MessageEntity m " +
            "WHERE (m.sender.id = :user_id OR m.receiver.id = :user_id) " +
            "GROUP BY m.sender.id, m.receiver.id " +
            "ORDER BY lastMessageDate DESC"
)


/**
 * Entity representing a message exchanged between users.
 * <p>
 * Indexes are added to optimize queries for unread messages, conversations, and message notifications.
 * <ul>
 *   <li>receiver_id, sender_id, is_read: For unread message lookups and marking conversations as read.</li>
 *   <li>receiver_id, sender_id, sent_date: For fetching conversations between two users, ordered by date.</li>
 *   <li>sender_id, receiver_id, sent_date: For fetching all messages sent by a user to another, ordered by date.</li>
 *   <li>receiver_id, is_read: For fast lookups of all unread messages for a user.</li>
 *   <li>sent_date: For global queries ordered by date (recent messages site-wide).</li>
 * </ul>
 */
@Entity
@Table(name="message",
    indexes = {
        /**
         * Index for unread message lookups and marking conversations as read.
         */
        @Index(name = "idx_recipient_sender_unread", columnList = "receiver_id, sender_id, is_read"),
        /**
         * Index for fetching conversations between two users, ordered by date.
         */
        @Index(name = "idx_conversation_pair", columnList = "receiver_id, sender_id, sent_date"),
        /**
         * Index for fetching all messages sent by a user to another, ordered by date.
         */
        @Index(name = "idx_sender_receiver_date", columnList = "sender_id, receiver_id, sent_date"),
        /**
         * Index for fast lookups of all unread messages for a user.
         */
        @Index(name = "idx_receiver_is_read", columnList = "receiver_id, is_read"),
        /**
         * Index for global queries ordered by date (recent messages site-wide).
         */
        @Index(name = "idx_sent_date", columnList = "sent_date")
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
    private Boolean messageIsRead;

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

    public Boolean getMessageIsRead() {
        return messageIsRead;
    }

    public void setMessageIsRead(Boolean messageIsRead) {
        this.messageIsRead = messageIsRead;
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