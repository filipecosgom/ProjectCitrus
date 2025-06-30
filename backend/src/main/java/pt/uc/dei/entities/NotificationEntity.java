package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.NotificationType;
import java.io.Serializable;
import java.time.LocalDateTime;


@NamedQuery(
        name = "NotificationEntity.getNotifications",
        query = "SELECT n " +
                "FROM NotificationEntity n " +
                "WHERE n.user.id = :id")

@NamedQuery(
        name = "NotificationEntity.getTotalNotifications",
        query = "SELECT COUNT(n) " +
                "FROM NotificationEntity n " +
                "WHERE n.user.id = :id")

@NamedQuery(
        name = "NotificationEntity.readNotification",
        query = "UPDATE NotificationEntity n " +
                "SET isRead = true " +
                "WHERE n.user.id = :userId " +
                "AND n.id = :notificationId")

@NamedQuery(
        name = "NotificationEntity.checkIfNotificationExist",
        query = "SELECT COUNT(n) " +
                "FROM NotificationEntity n " +
                "WHERE n.user.id = :userId " +
                "AND n.id = :notificationId")

@Entity
@Table(name = "notification", indexes = {
        @Index(name = "idx_notifications_recipient", columnList = "user"),
        @Index(name = "idx_notification_recipient_id", columnList = "user, id"),
        @Index(name = "idx_unread_messages", columnList = "user, type, isRead")
})
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

    /**
     * Gets the unique identifier for the notification.
     * @return the notification ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the notification.
     * @param id the notification ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the type of notification.
     * @return the notification type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Sets the type of notification.
     * @param type the notification type
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Gets the content of the notification.
     * @return the notification content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the notification.
     * @param content the notification content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the creation date and time of the notification.
     * @return the creation date and time
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date and time of the notification.
     * @param creationDate the creation date and time
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets whether the notification has been read.
     * @return true if read, false otherwise
     */
    public Boolean getRead() {
        return isRead;
    }

    /**
     * Sets whether the notification has been read.
     * @param read true if read, false otherwise
     */
    public void setRead(Boolean read) {
        isRead = read;
    }

    /**
     * Gets whether the notification has been seen.
     * @return true if seen, false otherwise
     */
    public Boolean getSeen() {
        return isSeen;
    }

    /**
     * Sets whether the notification has been seen.
     * @param seen true if seen, false otherwise
     */
    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    /**
     * Gets the number of associated messages.
     * @return the message count
     */
    public Integer getMessageCount() {
        return messageCount;
    }

    /**
     * Sets the number of associated messages.
     * @param messageCount the message count
     */
    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    /**
     * Gets the user associated with the notification.
     * @return the user entity
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets the user associated with the notification.
     * @param user the user entity
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }
}