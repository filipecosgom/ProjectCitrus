package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.NotificationType;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity representing a notification.
 * Stores details about notification type, content, status, and associated user.
 */
@Entity
@Table(name = "notification")
public class NotificationEntity implements Serializable {

    /**
     * The unique identifier for the notification.
     * Generated automatically.
     */
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The type of notification.
     * Stored as a string representation of the `NotificationType` enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = false, updatable = false)
    private NotificationType type;

    /**
     * The content of the notification.
     * Must always be sent and can be updated.
     */
    @Column(name = "content", nullable = false, unique = false, updatable = true)
    private String content;

    /**
     * The date and time when the notification was created.
     * Cannot be updated once set.
     */
    @Column(name = "creation_date", nullable = false, unique = false, updatable = false)
    private LocalDateTime creationDate;

    /**
     * Indicates whether the notification has been read.
     * Can be updated.
     */
    @Column(name = "is_read", nullable = false, unique = false, updatable = true)
    private Boolean isRead;

    @Column(name = "is_seen", nullable = false, unique = false, updatable = true)
    private Boolean isSeen;

    /**
     * The number of associated messages.
     * Must always be sent and can be updated.
     */
    @Column(name = "message_count", nullable = false, unique = false, updatable = true)
    private Integer messageCount;

    /**
     * The user associated with the notification.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity user;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}