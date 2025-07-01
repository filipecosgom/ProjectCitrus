package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a message.
 * Stores information related to message content, sender, receiver, and read status.
 */
public class MessageSendDTO {


    /**
     * The content of the message.
     * Must not be blank.
     */
    @NotBlank(message = "Message content missing")
    private String messageContent;

    /**
     * The unique identifier of the receiver.
     * Must not be null.
     */
    @NotNull(message = "Receiver missing")
    private Long receiverId;

    // Getters and Setters

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