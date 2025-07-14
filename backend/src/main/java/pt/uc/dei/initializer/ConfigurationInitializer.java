package pt.uc.dei.initializer;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import pt.uc.dei.entities.ConfigurationEntity;
import pt.uc.dei.repositories.ConfigurationRepository;
import pt.uc.dei.repositories.UserRepository;

import java.time.LocalDateTime;
/**
 * Initializes system configuration with default values if none exists.
 * <p>
 * Creates the first configuration entity with default time settings:
 * <ul>
 *   <li>Login expiration: 60 minutes</li>
 *   <li>Email verification: 48 hours (2880 minutes)</li>
 *   <li>Password reset: 48 hours (2880 minutes)</li>
 * </ul>
 *
 * Annotated with <b>@Singleton</b> to ensure a single instance manages configuration.
 */
@Singleton
public class ConfigurationInitializer {
    @EJB
    private ConfigurationRepository configurationRepository;
    @EJB
    private UserRepository userRepository;

    /**
     * Creates default configuration if none exists.
     * <p>
     * Links configuration to the default admin account.
     */
    public void initializeConfiguration() {
        if(configurationRepository.getLatestConfiguration() == null) {
            ConfigurationEntity newConfiguration = new ConfigurationEntity();
            newConfiguration.setLoginTime(60);          // 1 hour
            newConfiguration.setVerificationTime(2880); // 48 hours
            newConfiguration.setPasswordResetTime(2880);// 48 hours
            newConfiguration.setAdmin(userRepository.findUserByEmail("citrus.apiteam@gmail.com"));
            newConfiguration.setCreationDate(LocalDateTime.now());
            newConfiguration.setTwoFactorAuthEnabled(true);
            configurationRepository.persist(newConfiguration);
        }
    }
}