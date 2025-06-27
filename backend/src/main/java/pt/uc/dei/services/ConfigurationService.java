package pt.uc.dei.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.ConfigurationDTO;
import pt.uc.dei.entities.ConfigurationEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.ConfigurationRepository;
import pt.uc.dei.repositories.UserRepository;

import java.time.LocalDateTime;

/**
 * Service class for managing configuration-related operations.
 * <p>
 * Provides methods for retrieving the latest system configuration and creating new configurations.
 * Utilizes {@link ConfigurationRepository} for persistence and {@link UserRepository} for user-related operations.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
 */
@Stateless
public class ConfigurationService {
    /**
     * Logger instance for tracking configuration service operations.
     */
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationService.class);

    /**
     * Repository for accessing and modifying configuration data.
     */
    @Inject
    ConfigurationRepository configurationRepository;

    /**
     * Service for managing user-related operations.
     */
    @Inject
    UserService userService;

    /**
     * Repository for accessing user-related data.
     */
    @Inject
    UserRepository userRepository;

    /**
     * Default constructor for ConfigurationService.
     */
    public ConfigurationService() {
    }

    /**
     * Retrieves the latest configuration from the database.
     * <p>
     * Constructs a {@link ConfigurationDTO} object based on the latest persisted {@link ConfigurationEntity}.
     *
     * @return The latest {@link ConfigurationDTO} if found, null otherwise.
     */
    public ConfigurationDTO getLatestConfiguration() {
        try {
            ConfigurationEntity configurationEntity = configurationRepository.getLatestConfiguration();
            ConfigurationDTO configurationDto = new ConfigurationDTO();
            configurationDto.setId(configurationEntity.getId());
            configurationDto.setAdminId(configurationEntity.getAdmin().getId());
            configurationDto.setCreationDate(configurationEntity.getCreationDate());
            configurationDto.setPasswordResetTime(configurationEntity.getPasswordResetTime());
            configurationDto.setLoginTime(configurationEntity.getLoginTime());
            configurationDto.setVerificationTime(configurationEntity.getVerificationTime());
            return configurationDto;
        } catch (Exception e) {
            LOGGER.error("Error getting latest expiration configuration");
            return null;
        }
    }

    /**
     * Creates a new configuration entry in the database.
     *
     * @param configurationDto The configuration data transfer object containing the necessary configuration details
     * @return true if the configuration was successfully created, false otherwise
     */
    public boolean createNewConfiguration(ConfigurationDTO configurationDto) {
        try {
            ConfigurationEntity newConfiguration = new ConfigurationEntity();
            newConfiguration.setId(configurationDto.getId());
            newConfiguration.setCreationDate(LocalDateTime.now());
            newConfiguration.setPasswordResetTime(configurationDto.getPasswordResetTime());
            newConfiguration.setLoginTime(configurationDto.getLoginTime());
            newConfiguration.setVerificationTime(configurationDto.getVerificationTime());
            UserEntity admin = userRepository.findUserById(configurationDto.getAdminId());
            newConfiguration.setAdmin(admin);
            configurationRepository.persist(newConfiguration);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error setting new configuration by admin ID " + configurationDto.getAdminId());
            return false;
        }
    }
}