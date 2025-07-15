package pt.uc.dei.repositories;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.ConfigurationEntity;
/**
 * Repository class for handling persistence operations for {@link ConfigurationEntity}.
 * <p>
 * Provides data access methods specific to configuration entities, extending the basic
 * CRUD operations from {@link AbstractRepository}.
 *
 * @Stateless Marks this class as a stateless EJB, making it eligible for dependency injection
 * and transaction management by the EJB container.
 */
@Stateless
public class ConfigurationRepository extends AbstractRepository<ConfigurationEntity> {
    /**
     * Logger instance for tracking configuration repository operations.
     */
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationRepository.class);

    /**
     * Serial version UID for ensuring class consistency during serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new repository for the {@link ConfigurationEntity} class.
     */
    public ConfigurationRepository() {
        super(ConfigurationEntity.class);
    }

    /**
     * Retrieves the latest configuration from the database.
     * <p>
     * Uses a named query to fetch the most recent configuration entry.
     *
     * @return The latest {@link ConfigurationEntity} if found, null otherwise.
     */
    public ConfigurationEntity getLatestConfiguration() {
        try {
            ConfigurationEntity currentConfiguration = (ConfigurationEntity) em.createNamedQuery("Configuration.getLatestConfiguration")
                    .setMaxResults(1)
                    .getSingleResult();
            LOGGER.info("Current configuration: " + currentConfiguration);
            return currentConfiguration;
        } catch (NoResultException e) {
            LOGGER.warn("No configuration found.");
            return null;
        }
    }

    /**
     * Updates the two-factor authentication enabled status in the latest configuration.
     *
     * @param enabled The new status for two-factor authentication.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateTwoFactorAuthEnabled(boolean enabled) {
        ConfigurationEntity config = getLatestConfiguration();
        if (config == null) return false;
        config.setTwoFactorAuthEnabled(enabled);
        em.merge(config);
        return true;
    }

    /**
     * Retrieves the two-factor authentication enabled status from the latest configuration.
     *
     * @return The enabled status of two-factor authentication, or null if no configuration is found.
     */
    public Boolean getLatestTwoFactorAuthEnabled() {
        ConfigurationEntity config = getLatestConfiguration();
        return config != null ? config.getTwoFactorAuthEnabled() : null;
    }
}