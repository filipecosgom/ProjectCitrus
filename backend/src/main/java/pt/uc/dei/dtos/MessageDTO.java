package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a message.
 * Stores information related to message content, sender, receiver, and read status.
 */
public class MessageDTO {

    /**
     * The unique identifier for the message.
     */
    private Long id;

    /**
     * The date and time when the message was sent.
     * Must not be null.
     */
    @NotNull(message = "Sent date missing")
    private LocalDateTime sentDate;

    /**
     * Indicates whether the message has been read.
     * Must not be null.
     */
    private Boolean messageIsRead;

    /**
     * The content of the message.
     * Must not be blank.
     */
    @NotBlank(message = "Message content missing")
    private String messageContent;

    /**
     * The unique identifier of the sender.
     * Must not be null.
     */
    @NotNull(message = "Sender missing")
    private Long senderId;

    /**
     * The unique identifier of the receiver.
     * Must not be null.
     */
    @NotNull(message = "Receiver missing")
    private Long receiverId;

    // Getters and Setters

    /**
     * Retrieves the message ID.
     *
     * @return The message ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the message ID.
     *
     * @param id The message ID to be set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the date and time the message was sent.
     *
     * @return The sent date and time.
     */
    public LocalDateTime getSentDate() {
        return sentDate;
    }

    /**
     * Sets the sent date and time of the message.
     *
     * @param sentDate The sent date to be set.
     */
    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * Retrieves the read status of the message.
     *
     * @return `true` if read, `false` if unread.
     */
    public Boolean getMessageIsRead() {
        return messageIsRead;
    }

    /**
     * Sets the read status of the message.
     *
     * @param messageIsRead The read status to be set.
     */
    public void setMessageIsRead(Boolean messageIsRead) {
        this.messageIsRead = messageIsRead;
    }

    /**
     * Retrieves the message content.
     *
     * @return The message content.
     */
    public String getMessageContent() {
        return messageContent;
    }

    /**
     * Sets the message content.
     *
     * @param messageContent The content to be set.
     */
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    /**
     * Retrieves the sender ID.
     *
     * @return The sender ID.
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * Sets the sender ID.
     *
     * @param senderId The sender ID to be set.
     */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /**
     * Retrieves the receiver ID.
     *
     * @return The receiver ID.
     */
    public Long getReceiverId() {
        return receiverId;
    }

    /**
     * Sets the receiver ID.
     *
     * @param receiverId The receiver ID to be set.
     */
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    // Alias for recipientId
    public Long getRecipientId() {
        return getReceiverId();
    }
    public void setRecipientId(Long recipientId) {
        setReceiverId(recipientId);
    }

    // Alias for content
    public String getContent() {
        return getMessageContent();
    }
    public void setContent(String content) {
        setMessageContent(content);
    }
}