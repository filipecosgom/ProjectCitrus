
package pt.uc.dei.services;

import jakarta.ejb.Stateless;
import jakarta.ejb.Asynchronous; // ✅ ADICIONAR
import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.concurrent.Future; // ✅ ADICIONAR
import jakarta.ejb.AsyncResult; // ✅ ADICIONAR
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.config.EmailConfig;
import pt.uc.dei.config.MessageTemplate;
import pt.uc.dei.controllers.UserController;
import pt.uc.dei.dtos.ConfigurationDTO;
import pt.uc.dei.entities.NotificationEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.NotificationRepository;

import java.util.List;
import java.util.Properties;

/**
 * Service class responsible for sending various types of notification emails to users, managers, and administrators.
 * <p>
 * Utilizes the JavaMail API to send HTML-based emails for account activation, password reset, cycle notifications,
 * course assignments, profile updates, and chat/message notifications. Supports both synchronous and asynchronous
 * (rate-limited) email delivery to prevent server or firewall blocks in university environments.
 * <p>
 * Email configuration is loaded from environment variables and configuration services. Logging is provided for all
 * major actions and error conditions.
 */
@Stateless
public class EmailService {

    /**
     * Logger instance for tracking email events and errors.
     */
    private final Logger LOGGER = LogManager.getLogger(EmailService.class);

    /**
     * The email account used to send notification emails.
     * Retrieved from the system environment variables.
     */
    private String emailAccount = System.getenv("EMAIL");

    /**
     * The password for the email account.
     * Retrieved from the system environment variables.
     */
    private String password = System.getenv("PASSWORD");

    @Inject
    ConfigurationService configurationService;

    @EJB
    NotificationRepository notificationRepository;

    /**
     * Sends an activation email to the specified recipient.
     *
     * @param recipientEmail  The email address of the recipient.
     * @param activationToken The activation token for the recipient's account.
     * @param secretKey       The secret key for two-factor authentication (if applicable).
     * @param language        The language code for the email template (e.g., "en", "pt").
     */
    public void sendActivationEmail(String recipientEmail, String activationToken, String secretKey, String language) {
        try {
            // Retrieve SMTP properties for configuring email session
            Properties properties = EmailConfig.getSMTPProperties();

            // Create a new email session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAccount, password);
                }
            });

            // Construct the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            String activationLink = "https://localhost:3000/activate?token=" + activationToken + "&lang=" + language;
            ConfigurationDTO configurationDTO = configurationService.getLatestConfiguration();

            switch (language) {
                case "en": {
                    message.setSubject("CITRUS - Activate Your Account");
                    String messageBody = MessageTemplate.ACCOUNT_ACTIVATION_TEMPLATE_EN(activationLink,
                            (configurationDTO.getVerificationTime() / 60), secretKey);
                    message.setContent(messageBody, "text/html");

                }
                case "pt": {
                    message.setSubject("CITRUS - Ative a sua conta");
                    String messageBody = MessageTemplate.ACCOUNT_ACTIVATION_TEMPLATE_PT(activationLink,
                            (configurationDTO.getVerificationTime() / 60), secretKey);
                    message.setContent(messageBody, "text/html");
                }
            }
            Transport.send(message);
            LOGGER.info("Sending activation token: {} to: " + recipientEmail, activationToken);
        } catch (MessagingException e) {
            e.printStackTrace();
            LOGGER.error("Failed to send activation email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    /**
     * Sends a password reset email to the specified recipient.
     *
     * @param recipientEmail     The email address of the recipient.
     * @param passwordResetToken The password reset token for the recipient's account.
     * @param language           The language code for the email template (e.g., "en", "pt").
     */
    public void sendPasswordResetEmail(String recipientEmail, String passwordResetToken, String language) {
        try {
            // Retrieve SMTP properties for configuring email session
            Properties properties = EmailConfig.getSMTPProperties();

            // Create a new email session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAccount, password);
                }
            });

            // Construct the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            String resetLink = "https://localhost:3000/password-reset?token=" + passwordResetToken + "&lang="
                    + language;
            ConfigurationDTO configurationDTO = configurationService.getLatestConfiguration();
            switch (language) {
                case "en": {
                    message.setSubject("CITRUS - Reset your password");
                    String messageBody = MessageTemplate.PASSWORD_RESET_TEMPLATE_EN(resetLink,
                            (configurationDTO.getPasswordResetTime() / 60));
                    message.setContent(messageBody, "text/html");
                    break;

                }
                case "pt": {
                    message.setSubject("CITRUS - recupere a sua password");
                    String messageBody = MessageTemplate.PASSWORD_RESET_TEMPLATE_PT(resetLink,
                            (configurationDTO.getPasswordResetTime() / 60));
                    message.setContent(messageBody, "text/html");
                    break;
                }
            }

            // Send the email
            Transport.send(message);
            LOGGER.info("Sending password reset {} to: " + recipientEmail, passwordResetToken);
        } catch (MessagingException e) {
            e.printStackTrace();
            LOGGER.error("Failed to send password reset email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    /**
     * Sends a cycle open notification email to a manager or administrator.
     *
     * @param recipientEmail  The email address of the recipient.
     * @param cycleId         The ID of the created cycle.
     * @param startDate       The start date of the cycle.
     * @param endDate         The end date of the cycle.
     * @param adminName       The name of the admin who created the cycle.
     * @param appraisalsCount The number of appraisals created in the cycle.
     * @param language        The language code for the email template (e.g., "en", "pt").
     */
    public void sendCycleOpenNotificationEmail(String recipientEmail, String cycleId, String startDate,
            String endDate, String adminName, int appraisalsCount, String language) {
        try {
            // Retrieve SMTP properties for configuring email session
            Properties properties = EmailConfig.getSMTPProperties();

            // Create a new email session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAccount, password);
                }
            });

            // Construct the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            String cycleLink = "https://localhost:3000/appraisals";

            switch (language) {
                case "en": {
                    message.setSubject("CITRUS - New Performance Cycle Started");
                    String messageBody = MessageTemplate.CYCLE_NOTIFICATION_TEMPLATE_EN(
                            cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
                    message.setContent(messageBody, "text/html");
                    break;
                }
                case "pt": {
                    message.setSubject("CITRUS - Novo Ciclo de Avaliação Iniciado");
                    String messageBody = MessageTemplate.CYCLE_NOTIFICATION_TEMPLATE_PT(
                            cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
                    message.setContent(messageBody, "text/html");
                    break;
                }
                default: {
                    message.setSubject("CITRUS - New Performance Cycle Started");
                    String messageBody = MessageTemplate.CYCLE_NOTIFICATION_TEMPLATE_EN(
                            cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
                    message.setContent(messageBody, "text/html");
                    break;
                }
            }

            // Send the email
            Transport.send(message);
            LOGGER.info("Cycle notification sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send cycle notification email to {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Failed to send cycle notification email", e);
        }
    }

    /**
     * Asynchronously sends cycle open notification emails to a list of recipients (managers/admins) with ultra-conservative rate limiting.
     * <p>
     * This method is designed for university environments to avoid firewall or SMTP server blocks. It sends emails one by one,
     * with delays between each email and between batches, and logs all progress and errors.
     *
     * @param cycleId         The ID of the created cycle.
     * @param startDate       The start date of the cycle.
     * @param endDate         The end date of the cycle.
     * @param adminName       The name of the admin who created the cycle.
     * @param appraisalsCount The number of appraisals created in the cycle.
     * @param recipients      The list of user entities to notify.
     * @return Future<Boolean> indicating if all emails were sent successfully.
     */
    @Asynchronous
    public Future<Boolean> sendCycleOpenNotificationEmailsAsync(
            String cycleId, String startDate, String endDate,
            String adminName, int appraisalsCount,
            java.util.List<UserEntity> recipients) {

        LOGGER.info("🔄 Starting ASYNC email notification process for cycle {}", cycleId);

        try {
            boolean allEmailsSent = true;
            int emailsSent = 0;
            int emailsFailed = 0;

            // ✅ RATE LIMITING ULTRA-CONSERVADOR para ambiente universitário
            final int MAX_EMAILS_PER_BATCH = 1; // 1 email por lote
            final long DELAY_BETWEEN_BATCHES_MS = 180000; // 3 minutos entre lotes
            final long DELAY_BETWEEN_EMAILS_MS = 10000; // 10 segundos base

            LOGGER.info("📧 Will send {} emails with ultra-conservative rate limiting", recipients.size());
            LOGGER.info("📧 Rate: 1 email every 3 minutes (max 20 emails/hour)");

            for (int i = 0; i < recipients.size(); i++) {
                UserEntity user = recipients.get(i);

                try {
                    // ✅ DELAY progressivo - aumenta com cada email
                    long delayMs = DELAY_BETWEEN_EMAILS_MS + (i * 1000); // +1s por cada email

                    if (i > 0) {
                        LOGGER.info("⏳ Waiting {} ms before sending email {}/{}...",
                                delayMs, i + 1, recipients.size());
                        Thread.sleep(delayMs);
                    }

                    // ✅ DELAY extra a cada lote
                    if (i > 0 && i % MAX_EMAILS_PER_BATCH == 0) {
                        LOGGER.info("⏳ Batch delay: waiting {} ms before next batch...",
                                DELAY_BETWEEN_BATCHES_MS);
                        Thread.sleep(DELAY_BETWEEN_BATCHES_MS);
                    }

                    String userLanguage = "en"; // Default language

                    LOGGER.info("📤 Sending cycle notification to {} ({}/{})",
                            user.getEmail(), i + 1, recipients.size());

                    // ✅ Usar o método síncrono existente
                    sendCycleOpenNotificationEmail(
                            user.getEmail(),
                            cycleId,
                            startDate,
                            endDate,
                            adminName,
                            appraisalsCount,
                            userLanguage);

                    emailsSent++;
                    LOGGER.info("✅ Email {}/{} sent successfully to: {}",
                            i + 1, recipients.size(), user.getEmail());

                } catch (InterruptedException e) {
                    LOGGER.error("❌ Email sending process was interrupted");
                    Thread.currentThread().interrupt();
                    allEmailsSent = false;
                    break;
                } catch (Exception e) {
                    emailsFailed++;
                    allEmailsSent = false;
                    LOGGER.error("❌ Failed to send email {}/{} to {}: {}",
                            i + 1, recipients.size(), user.getEmail(), e.getMessage());

                    // ✅ Continue com outros emails mesmo se um falhar
                    continue;
                }
            }

            LOGGER.info("📧 ASYNC notification completed - Sent: {}, Failed: {}, Total: {}",
                    emailsSent, emailsFailed, recipients.size());

            return new AsyncResult<>(allEmailsSent);

        } catch (Exception e) {
            LOGGER.error("❌ Critical error in async email sending: {}", e.getMessage(), e);
            return new AsyncResult<>(false);
        }
    }

    /**
     * Sends a cycle close notification email to a manager or administrator.
     *
     * @param recipientEmail  The email address of the recipient.
     * @param cycleId         The ID of the closed cycle.
     * @param startDate       The start date of the cycle.
     * @param endDate         The end date of the cycle.
     * @param adminName       The name of the admin who closed the cycle.
     * @param appraisalsCount The number of appraisals in the cycle.
     * @param language        The language code for the email template (e.g., "en", "pt").
     */
    public void sendCycleCloseNotificationEmail(String recipientEmail, String cycleId, String startDate,
            String endDate, String adminName, int appraisalsCount, String language) {
        try {
            // Retrieve SMTP properties for configuring email session
            Properties properties = EmailConfig.getSMTPProperties();

            // Create a new email session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAccount, password);
                }
            });

            // Construct the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            String cycleLink = "https://localhost:3000/appraisals";

            switch (language) {
                case "en": {
                    message.setSubject("CITRUS - Performance Cycle Ended");
                    String messageBody = MessageTemplate.CYCLE_END_NOTIFICATION_TEMPLATE_EN(
                            cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
                    message.setContent(messageBody, "text/html");
                    break;
                }
                case "pt": {
                    message.setSubject("CITRUS - Ciclo de Avaliação Encerrado");
                    String messageBody = MessageTemplate.CYCLE_END_NOTIFICATION_TEMPLATE_PT(
                            cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
                    message.setContent(messageBody, "text/html");
                    break;
                }
                default: {
                    message.setSubject("CITRUS - New Performance Cycle Started");
                    String messageBody = MessageTemplate.CYCLE_END_NOTIFICATION_TEMPLATE_EN(
                            cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink);
                    message.setContent(messageBody, "text/html");
                    break;
                }
            }

            // Send the email
            Transport.send(message);
            LOGGER.info("Cycle notification sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send cycle notification email to {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Failed to send cycle notification email", e);
        }
    }

    /**
     * Asynchronously sends cycle close notification emails to a list of recipients (managers/admins) with ultra-conservative rate limiting.
     * <p>
     * This method is designed for university environments to avoid firewall or SMTP server blocks. It sends emails one by one,
     * with delays between each email and between batches, and logs all progress and errors.
     *
     * @param cycleId         The ID of the closed cycle.
     * @param startDate       The start date of the cycle.
     * @param endDate         The end date of the cycle.
     * @param adminName       The name of the admin who closed the cycle.
     * @param appraisalsCount The number of appraisals in the cycle.
     * @param recipients      The list of user entities to notify.
     * @return Future<Boolean> indicating if all emails were sent successfully.
     */
    @Asynchronous
    public Future<Boolean> sendCycleCloseNotificationEmailsAsync(
            String cycleId, String startDate, String endDate,
            String adminName, int appraisalsCount,
            java.util.List<UserEntity> recipients) {

        LOGGER.info("🔄 Starting ASYNC email notification process for cycle {}", cycleId);

        try {
            boolean allEmailsSent = true;
            int emailsSent = 0;
            int emailsFailed = 0;

            // ✅ RATE LIMITING ULTRA-CONSERVADOR para ambiente universitário
            final int MAX_EMAILS_PER_BATCH = 1; // 1 email por lote
            final long DELAY_BETWEEN_BATCHES_MS = 180000; // 3 minutos entre lotes
            final long DELAY_BETWEEN_EMAILS_MS = 10000; // 10 segundos base

            LOGGER.info("📧 Will send {} emails with ultra-conservative rate limiting", recipients.size());
            LOGGER.info("📧 Rate: 1 email every 3 minutes (max 20 emails/hour)");

            for (int i = 0; i < recipients.size(); i++) {
                UserEntity user = recipients.get(i);

                try {
                    // ✅ DELAY progressivo - aumenta com cada email
                    long delayMs = DELAY_BETWEEN_EMAILS_MS + (i * 1000); // +1s por cada email

                    if (i > 0) {
                        LOGGER.info("⏳ Waiting {} ms before sending email {}/{}...",
                                delayMs, i + 1, recipients.size());
                        Thread.sleep(delayMs);
                    }

                    // ✅ DELAY extra a cada lote
                    if (i > 0 && i % MAX_EMAILS_PER_BATCH == 0) {
                        LOGGER.info("⏳ Batch delay: waiting {} ms before next batch...",
                                DELAY_BETWEEN_BATCHES_MS);
                        Thread.sleep(DELAY_BETWEEN_BATCHES_MS);
                    }

                    String userLanguage = "en"; // Default language

                    LOGGER.info("📤 Sending cycle notification to {} ({}/{})",
                            user.getEmail(), i + 1, recipients.size());

                    // ✅ Usar o método síncrono existente
                    sendCycleCloseNotificationEmail(
                            user.getEmail(),
                            cycleId,
                            startDate,
                            endDate,
                            adminName,
                            appraisalsCount,
                            userLanguage);

                    emailsSent++;
                    LOGGER.info("✅ Email {}/{} sent successfully to: {}",
                            i + 1, recipients.size(), user.getEmail());

                } catch (InterruptedException e) {
                    LOGGER.error("❌ Email sending process was interrupted");
                    Thread.currentThread().interrupt();
                    allEmailsSent = false;
                    break;
                } catch (Exception e) {
                    emailsFailed++;
                    allEmailsSent = false;
                    LOGGER.error("❌ Failed to send email {}/{} to {}: {}",
                            i + 1, recipients.size(), user.getEmail(), e.getMessage());

                    // ✅ Continue com outros emails mesmo se um falhar
                    continue;
                }
            }

            LOGGER.info("📧 ASYNC notification completed - Sent: {}, Failed: {}, Total: {}",
                    emailsSent, emailsFailed, recipients.size());

            return new AsyncResult<>(allEmailsSent);

        } catch (Exception e) {
            LOGGER.error("❌ Critical error in async email sending: {}", e.getMessage(), e);
            return new AsyncResult<>(false);
        }
    }

    /**
     * Sends a notification email to a user when their profile is updated by a manager.
     *
     * @param recipientEmail The email address of the user whose profile was updated.
     * @param managerName    The name of the manager who performed the update.
     * @param userName       The name of the user whose profile was updated.
     * @param userId         The ID of the user whose profile was updated.
     * @param date           The date of the update.
     */
    public void sendUserUpdateNotificationEmail(String recipientEmail, String managerName, String userName,
            Long userId, String date) {
        try {
            // Retrieve SMTP properties for configuring email session
            Properties properties = EmailConfig.getSMTPProperties();

            // Create a new email session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAccount, password);
                }
            });
            // Construct the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            String profileLink = "https://localhost:3000/profile?id=" + userId;
            message.setSubject("CITRUS - " + userName + " profile updated");
            String messageBody = MessageTemplate.PROFILE_UPDATE_NOTIFICATION_TEMPLATE_EN(
                    managerName, userName, date, profileLink);
            message.setContent(messageBody, "text/html");
            Transport.send(message);
            LOGGER.info("Cycle notification sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send cycle notification email to {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Failed to send cycle notification email", e);
        }
    }

    /**
     * Sends a notification email to a user when a new course is assigned to them by a manager.
     *
     * @param recipientEmail The email address of the user assigned to the course.
     * @param userName       The name of the user assigned to the course.
     * @param managerName    The name of the manager assigning the course.
     * @param courseName     The name of the new course.
     * @param userId         The ID of the user assigned to the course.
     */
    public void sendNewCourseNotificationEmail(String recipientEmail, String userName, String managerName,
            String courseName, Long userId) {
        try {
            // Retrieve SMTP properties for configuring email session
            Properties properties = EmailConfig.getSMTPProperties();

            // Create a new email session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAccount, password);
                }
            });
            // Construct the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailAccount));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            String courseLink = "https://localhost:3000/profile?id=" + userId + "&tab=training";
            String date = LocalDate.now().toString();
            message.setSubject("CITRUS - " + userName + " new course added");
            String messageBody = MessageTemplate.COURSE_ASSIGNMENT_NOTIFICATION_TEMPLATE_EN(
                    userName, managerName, courseName, date, courseLink);
            message.setContent(messageBody, "text/html");
            Transport.send(message);
            LOGGER.info("Course notification sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send course notification email to {}: {}", recipientEmail, e.getMessage());
            throw new RuntimeException("Failed to send cycle notification email", e);
        }
    }

    /**
     * Asynchronously sends chat/message notification emails to users with ultra-conservative rate limiting.
     * <p>
     * This method is designed for university environments to avoid firewall or SMTP server blocks. It sends emails one by one,
     * with delays between each email and between batches, and logs all progress and errors. Each notification is marked as sent in the database.
     *
     * @param notifications The list of NotificationEntity objects representing chat/message notifications to send.
     * @return Future<Boolean> indicating if all emails were sent successfully.
     */
    @Asynchronous
    public Future<Boolean> sendMessageNotificationEmailsAsync(List<NotificationEntity> notifications) {
        LOGGER.info("🔄 Starting ASYNC email notification process for chat/message notifications");

        try {
            boolean allEmailsSent = true;
            int emailsSent = 0;
            int emailsFailed = 0;

            final int MAX_EMAILS_PER_BATCH = 1;
            final long DELAY_BETWEEN_BATCHES_MS = 180000; // 3 minutes
            final long DELAY_BETWEEN_EMAILS_MS = 10000;   // 10 seconds

            LOGGER.info("📧 Will send {} chat/message emails with ultra-conservative rate limiting", notifications.size());
            LOGGER.info("📧 Rate: 1 email every 3 minutes (max 20 emails/hour)");

            for (int i = 0; i < notifications.size(); i++) {
                NotificationEntity notification = notifications.get(i);
                try {
                    long delayMs = DELAY_BETWEEN_EMAILS_MS + (i * 1000);

                    if (i > 0) {
                        LOGGER.info("⏳ Waiting {} ms before sending email {}/{}...",
                                delayMs, i + 1, notifications.size());
                        Thread.sleep(delayMs);
                    }

                    if (i > 0 && i % MAX_EMAILS_PER_BATCH == 0) {
                        LOGGER.info("⏳ Batch delay: waiting {} ms before next batch...",
                                DELAY_BETWEEN_BATCHES_MS);
                        Thread.sleep(DELAY_BETWEEN_BATCHES_MS);
                    }

                    UserEntity recipient = notification.getUser();
                    UserEntity sender = notification.getSender();
                    String recipientEmail = recipient.getEmail();
                    String recipientName = recipient.getName();
                    String senderName = sender.getName() + " " + sender.getSurname();
                    String chatLink = "https://localhost:3000/messages?id=" + sender.getId();

                    // Build and send the email
                    Properties properties = EmailConfig.getSMTPProperties();
                    Session session = Session.getInstance(properties, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailAccount, password);
                        }
                    });

                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(emailAccount));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

                    message.setSubject("CITRUS - Novo pedido de chat");
                    String messageBody = MessageTemplate.CHAT_REQUEST_NOTIFICATION_TEMPLATE_EN(
                            recipientName, senderName, chatLink
                    );
                    message.setContent(messageBody, "text/html");

                    Transport.send(message);
                    notification.setEmailSent(true);
                    notificationRepository.merge(notification);
                    emailsSent++;
                    LOGGER.info("✅ Chat/message email {}/{} sent successfully to: {}",
                            i + 1, notifications.size(), recipientEmail);

                } catch (InterruptedException e) {
                    LOGGER.error("❌ Email sending process was interrupted");
                    Thread.currentThread().interrupt();
                    allEmailsSent = false;
                    break;
                } catch (Exception e) {
                    emailsFailed++;
                    allEmailsSent = false;
                    LOGGER.error("❌ Failed to send chat/message email {}/{}: {}",
                            i + 1, notifications.size(), e.getMessage());
                    continue;
                }
            }

            LOGGER.info("📧 ASYNC chat/message notification completed - Sent: {}, Failed: {}, Total: {}",
                    emailsSent, emailsFailed, notifications.size());

            return new AsyncResult<>(allEmailsSent);

        } catch (Exception e) {
            LOGGER.error("❌ Critical error in async chat/message email sending: {}", e.getMessage(), e);
            return new AsyncResult<>(false);
        }
    }

    /**
     * Creates and returns SMTP properties with connection, read, and write timeouts configured.
     * <p>
     * These properties are used to prevent email sending from blocking indefinitely due to network or server issues.
     *
     * @return Properties object with SMTP host, port, authentication, TLS, timeouts, and security settings.
     */
    private Properties createEmailPropertiesWithTimeouts() {
        Properties props = new Properties();

        // Base configuration
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Timeouts to prevent blocking
        props.put("mail.smtp.connectiontimeout", "15000"); // 15 seconds to connect
        props.put("mail.smtp.timeout", "15000"); // 15 seconds for response
        props.put("mail.smtp.writetimeout", "15000"); // 15 seconds for write

        // Security configurations
        props.put("mail.smtp.sendpartial", "true"); // Allow partial send
        props.put("mail.smtp.quitwait", "false"); // Do not wait for QUIT response

        return props;
    }
}