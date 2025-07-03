// ConversationPreviewDTO.java
package pt.uc.dei.dtos;

import java.time.LocalDateTime;

/**
 * DTO para preview de conversas no dropdown de mensagens
 */
public class ConversationPreviewDTO {
    private Long userId;
    private String userName;
    private String userSurname;
    private Boolean hasAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageDate;
    private Boolean isLastMessageRead;
    private Integer unreadCount;
    private Boolean isLastMessageFromMe; // Para saber se foi eu que enviei a última mensagem
    
    // Constructors
    public ConversationPreviewDTO() {}
    
    public ConversationPreviewDTO(Long userId, String userName, String userSurname, 
                                 Boolean hasAvatar, String lastMessage, 
                                 LocalDateTime lastMessageDate, Boolean isLastMessageRead,
                                 Integer unreadCount, Boolean isLastMessageFromMe) {
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.hasAvatar = hasAvatar;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.isLastMessageRead = isLastMessageRead;
        this.unreadCount = unreadCount;
        this.isLastMessageFromMe = isLastMessageFromMe;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserSurname() { return userSurname; }
    public void setUserSurname(String userSurname) { this.userSurname = userSurname; }
    
    public Boolean getHasAvatar() { return hasAvatar; }
    public void setHasAvatar(Boolean hasAvatar) { this.hasAvatar = hasAvatar; }
    
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    
    public LocalDateTime getLastMessageDate() { return lastMessageDate; }
    public void setLastMessageDate(LocalDateTime lastMessageDate) { this.lastMessageDate = lastMessageDate; }
    
    public Boolean getIsLastMessageRead() { return isLastMessageRead; }
    public void setIsLastMessageRead(Boolean isLastMessageRead) { this.isLastMessageRead = isLastMessageRead; }
    
    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }
    
    public Boolean getIsLastMessageFromMe() { return isLastMessageFromMe; }
    public void setIsLastMessageFromMe(Boolean isLastMessageFromMe) { this.isLastMessageFromMe = isLastMessageFromMe; }
}