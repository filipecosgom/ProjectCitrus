package pt.uc.dei.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a message.
 * Stores details about sender, receiver, content, and status.
 */
@Entity
@Table(name = "message")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }
}