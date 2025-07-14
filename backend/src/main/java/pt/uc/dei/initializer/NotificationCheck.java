package pt.uc.dei.initializer;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.repositories.NotificationRepository;
import pt.uc.dei.services.EmailService;
import java.util.List;

/**
 * Checks for unsent notifications and triggers email sending at startup.
 * Annotated with @Singleton to ensure single initialization.
 */
@Singleton
public class NotificationCheck {
    @EJB
    private NotificationRepository notificationRepository;

    @EJB
    private EmailService emailService;

    /**
     * Checks for unsent notifications and triggers email sending.
     */
    public void checkAndSendUnemailedNotifications() {
        List<NotificationEntity> unsentNotifications = notificationRepository.getUnemailedMessageNotifications();
        if (unsentNotifications != null && !unsentNotifications.isEmpty()) {
            emailService.sendMessageNotificationEmailsAsync(unsentNotifications);
        }
    }
}
