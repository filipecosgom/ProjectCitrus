package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotNull;

public class NotificationUpdateDTO {
    @NotNull
    private Long notificationId;
    // isRead and isSeen are optional (nullable) for partial update, so no @NotNull here
    private Boolean notificationIsRead;
    private Boolean notificationIsSeen;

    public NotificationUpdateDTO() {}

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
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
