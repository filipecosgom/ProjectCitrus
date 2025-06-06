package pt.uc.dei.services;
import jakarta.ejb.Stateless;
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
import pt.uc.dei.controllers.UserController;

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

    /**
     * The email template for account activation.
     * Contains a link that allows users to activate their account.
     */
    private final String template = "<h1>Welcome to CITRUS - Critical Training, Review & User System.</h1>"
            + "<p>Click the link below to activate your account.</p><br>"
            + "<a href='https://localhost:3000/activate?token={{token}}'>Activate Account</a>";

    /**
     * Sends an activation email to the specified recipient.
     *
     * @param recipientEmail  The email address of the recipient.
     * @param activationToken The activation token for the recipient's account.
     */
    public void sendActivationEmail(String recipientEmail, String activationToken) {
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
            message.setSubject("CITRUS - Activate Your Account");


            // Replace placeholder with actual activation token
            String messageBody = template.replace("{{token}}", activationToken);
            message.setContent(messageBody, "text/html");

            // Send the email
            //Transport.send(message); ---TO UNCOMMENT!!!
            LOGGER.info("Sending activation token: {} to: " + recipientEmail, activationToken);
        } catch (MessagingException e) {
            e.printStackTrace();
            LOGGER.error("Failed to send activation email to {}: {}", recipientEmail, e.getMessage());
        }
    }


    public void sendPassworResetEmail(String recipientEmail, String passwordResetToken) {
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
            message.setSubject("CITRUS - Reset your password");


            // Replace placeholder with actual activation token
            String messageBody = template.replace("{{token}}", passwordResetToken);
            message.setContent(messageBody, "text/html");

            // Send the email
            Transport.send(message);
            LOGGER.info("Sending activation token: {} to: " + recipientEmail, passwordResetToken);
        } catch (MessagingException e) {
            e.printStackTrace();
            LOGGER.error("Failed to send activation email to {}: {}", recipientEmail, e.getMessage());
        }
    }
}