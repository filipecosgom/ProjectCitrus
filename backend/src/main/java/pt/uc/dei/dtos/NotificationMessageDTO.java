package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a notification.
 * Contains details such as notification type, content, timestamps, and recipient information.
 */
public class NotificationMessageDTO {

    /**
     * The unique identifier for the notification.
     * Must always be sent.
     */
    @NotNull(message = "Notification ID is required")
    private Long id;

    /**
     * The type of the notification.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Notification type missing")
    private String type;

    /**
     * The content of the notification.
     * Must always be sent and must not be blank.
     */
    @NotBlank(message = "Content missing")
    private String content;

    /**
     * The date and time when the notification was created.
     * Must always be sent.
     */
    @NotNull(message = "Creation date is required")
    private LocalDateTime creationDate;

    /**
     * Indicates whether the notification has been read.
     * Must always be sent.
     */
    @NotNull(message = "Read status is required")
    private Boolean isRead;

    @NotNull(message = "Seen status is required")
    private Boolean isSeen;

    /**
     * The number of messages associated with the notification.
     * Must always be sent.
     */
    @NotNull(message = "Message count is required")
    private Integer messageCount;

    private Long senderId;
    private Long recipientId;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
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
}