package pt.uc.dei.entities;

import jakarta.persistence.*;
import pt.uc.dei.enums.NotificationType;
import java.io.Serializable;
import java.time.LocalDateTime;

@NamedQuery(name = "NotificationEntity.getNotifications", query = "SELECT n " +
        "FROM NotificationEntity n " +
        "WHERE n.user.id = :id " +
        "ORDER BY n.creationDate DESC")

@NamedQuery(name = "NotificationEntity.getTotalNotifications", query = "SELECT COUNT(n) " +
        "FROM NotificationEntity n " +
        "WHERE n.user.id = :id")

@NamedQuery(name = "NotificationEntity.readNotification", query = "UPDATE NotificationEntity n " +
        "SET notificationIsRead = true " +
        "WHERE n.user.id = :userId " +
        "AND n.id = :notificationId")

@NamedQuery(name = "NotificationEntity.checkIfNotificationExist", query = "SELECT COUNT(n) " +
        "FROM NotificationEntity n " +
        "WHERE n.user.id = :userId " +
        "AND n.id = :notificationId")
@NamedQuery(name = "NotificationEntity.getMessageNotification", query = "SELECT n FROM NotificationEntity n " +
        "WHERE n.user.id = :recipientId " +
        "AND n.sender.id = :senderId " +
        "AND n.type = NotificationType.MESSAGE " +
        "AND n.notificationIsSeen = false")


/**
 * Entity representing a notification for a user.
 * <p>
 * Indexes are added to optimize queries for notification retrieval, unread/seen status, and ordering by creation date.
 * <ul>
 *   <li>user_id: For all queries filtering by user (recipient).</li>
 *   <li>user_id, id: For queries checking notification existence or fetching by user and notification id.</li>
 *   <li>user_id, type, is_read: For unread message notification queries and marking as read.</li>
 *   <li>user_id, type, is_seen: For queries involving seen status.</li>
 *   <li>user_id, creation_date: For ordering notifications by creation date for a user.</li>
 * </ul>
 */
@Entity
@Table(name = "notification",
    indexes = {
        /**
         * Index for all queries filtering by user (recipient).
         */
        @Index(name = "idx_notification_user_id", columnList = "user_id"),
        /**
         * Index for queries checking notification existence or fetching by user and notification id.
         */
        @Index(name = "idx_notification_user_id_id", columnList = "user_id, id"),
        /**
         * Index for unread message notification queries and marking as read.
         */
        @Index(name = "idx_notification_user_type_is_read", columnList = "user_id, type, is_read"),
        /**
         * Index for queries involving seen status.
         */
        @Index(name = "idx_notification_user_type_is_seen", columnList = "user_id, type, is_seen"),
        /**
         * Index for ordering notifications by creation date for a user.
         */
        @Index(name = "idx_notification_user_creation_date", columnList = "user_id, creation_date")
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
     * Indicates whether the notification has been read and clicked by the user.
     * Can be updated.
     */
    @Column(name = "is_read", nullable = false)
    private Boolean notificationIsRead;

    /**
     * Is seen indicates a notifications which content has been seen by the user.
     * This is different from read, as it indicates that the user has viewed the
     * notification content.
     * Can be updated.
     */
    @Column(name = "is_seen", nullable = false)
    private Boolean notificationIsSeen;

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

    /**
     * The sender of the notification.
     * Many-to-one relationship with `UserEntity`.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false, updatable = false)
    private UserEntity sender;

    // Getters and Setters

    /**
     * Gets the unique identifier for the notification.
     * 
     * @return the notification ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the notification.
     * 
     * @param id the notification ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the type of notification.
     * 
     * @return the notification type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Sets the type of notification.
     * 
     * @param type the notification type
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Gets the content of the notification.
     * 
     * @return the notification content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the notification.
     * 
     * @param content the notification content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the creation date and time of the notification.
     * 
     * @return the creation date and time
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date and time of the notification.
     * 
     * @param creationDate the creation date and time
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the number of associated messages.
     * 
     * @return the message count
     */
    public Integer getMessageCount() {
        return messageCount;
    }

    /**
     * Sets the number of associated messages.
     * 
     * @param messageCount the message count
     */
    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    /**
     * Gets the user associated with the notification.
     * 
     * @return the user entity
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets the user associated with the notification.
     * 
     * @param user the user entity
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * Gets the sender of the notification.
     * 
     * @return the sender entity
     */
    public UserEntity getSender() {
        return sender;
    }

    /**
     * Sets the sender of the notification.
     * 
     * @param sender the sender entity
     */
    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    // Convenience for recipientId (user)
    public Long getRecipientId() {
        return user != null ? user.getId() : null;
    }

    public void setRecipientId(Long recipientId) {
        // This should be set via setUser(UserEntity), but for DTO mapping, we allow
        // this setter
        if (this.user == null)
            this.user = new UserEntity();
        this.user.setId(recipientId);
    }

    // Alias for timestamp
    public LocalDateTime getTimestamp() {
        return getCreationDate();
    }

    public void setTimestamp(LocalDateTime timestamp) {
        setCreationDate(timestamp);
    }

    // Alias for unreadCount
    public Integer getUnreadCount() {
        return getMessageCount();
    }

    public void setUnreadCount(Integer unreadCount) {
        setMessageCount(unreadCount);
    }

    public Boolean getNotificationIsRead() {
        return notificationIsRead;
    }

    public void setNotificationIsRead(Boolean notificationIsRead) {
        this.notificationIsRead = notificationIsRead;
    }

    public Boolean getNotificationIsSeen() {
        return notificationIsSeen;
    }

    public void setNotificationIsSeen(Boolean notificationIsSeen) {
        this.notificationIsSeen = notificationIsSeen;
    }
}