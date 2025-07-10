package pt.uc.dei.services;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
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

import java.util.Properties;

/**
 * Service class responsible for sending activation emails to users.
 * Utilizes JavaMail API to send HTML-based emails containing an activation link.
 */
@Stateless
public class EmailService {

    /**
     * Logger instance for tracking registration events.
     */
    private final Logger LOGGER = LogManager.getLogger(EmailService.class);

    /**
     * The email account used to send activation emails.
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


    /**
     * Sends an activation email to the specified recipient.
     *
     * @param recipientEmail  The email address of the recipient.
     * @param activationToken The activation token for the recipient's account.
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
                    String messageBody = MessageTemplate.ACCOUNT_ACTIVATION_TEMPLATE_EN(activationLink, (configurationDTO.getVerificationTime()/60), secretKey);
                    message.setContent(messageBody, "text/html");

                }
                case "pt": {
                    message.setSubject("CITRUS - Ative a sua conta");
                    String messageBody = MessageTemplate.ACCOUNT_ACTIVATION_TEMPLATE_PT(activationLink, (configurationDTO.getVerificationTime()/60), secretKey);
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
     * @param recipientEmail The email address of the recipient
     * @param passwordResetToken The password reset token for the recipient's account
     * @param language The language code for the email template
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

            String resetLink = "https://localhost:3000/password-reset?token=" + passwordResetToken + "&lang=" + language;
            ConfigurationDTO configurationDTO = configurationService.getLatestConfiguration();
            switch (language) {
                case "en": {
                    message.setSubject("CITRUS - Reset your password");
                    String messageBody = MessageTemplate.PASSWORD_RESET_TEMPLATE_EN(resetLink, (configurationDTO.getPasswordResetTime()/60));
                    message.setContent(messageBody, "text/html");
                    break;

                }
                case "pt": {
                    message.setSubject("CITRUS - recupere a sua password");
                    String messageBody = MessageTemplate.PASSWORD_RESET_TEMPLATE_PT(resetLink, (configurationDTO.getPasswordResetTime()/60));
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
     * Sends cycle notification emails to managers and administrators.
     *
     * @param recipientEmail The email address of the recipient
     * @param cycleId The ID of the created cycle
     * @param startDate The start date of the cycle
     * @param endDate The end date of the cycle
     * @param adminName The name of the admin who created the cycle
     * @param appraisalsCount The number of appraisals created in the cycle
     * @param language The language code for the email template
     */
    public void sendCycleNotificationEmail(String recipientEmail, String cycleId, String startDate,
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

            String cycleLink = "https://localhost:3000/cycles?cycle=" + cycleId + "&lang=" + language;

            switch (language) {
                case "en": {
                    message.setSubject("CITRUS - New Performance Cycle Started");
                    String messageBody = MessageTemplate.CYCLE_NOTIFICATION_TEMPLATE_EN(
                        cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink
                    );
                    message.setContent(messageBody, "text/html");
                    break;
                }
                case "pt": {
                    message.setSubject("CITRUS - Novo Ciclo de Avaliação Iniciado");
                    String messageBody = MessageTemplate.CYCLE_NOTIFICATION_TEMPLATE_PT(
                        cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink
                    );
                    message.setContent(messageBody, "text/html");
                    break;
                }
                default: {
                    message.setSubject("CITRUS - New Performance Cycle Started");
                    String messageBody = MessageTemplate.CYCLE_NOTIFICATION_TEMPLATE_EN(
                        cycleId, startDate, endDate, adminName, appraisalsCount, cycleLink
                    );
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
}