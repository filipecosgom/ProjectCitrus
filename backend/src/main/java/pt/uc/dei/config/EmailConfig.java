package pt.uc.dei.config;

import java.util.Properties;

/**
 * Configuration class for SMTP email settings.
 * Provides the necessary properties for authenticating and sending emails via Gmail's SMTP server.
 */
public class EmailConfig {

    /**
     * Retrieves the SMTP properties required for sending emails.
     *
     * @return A {@link Properties} object containing SMTP configuration settings.
     */
    public static Properties getSMTPProperties() {
        Properties properties = new Properties();

        // Enables authentication for the SMTP server
        properties.put("mail.smtp.auth", "true");

        // Enables STARTTLS encryption for secure communication
        properties.put("mail.smtp.starttls.enable", "true");

        // Specifies the SMTP server host
        properties.put("mail.smtp.host", "smtp.gmail.com");

        // Defines the SMTP server port
        properties.put("mail.smtp.port", "587");

        return properties;
    }
}