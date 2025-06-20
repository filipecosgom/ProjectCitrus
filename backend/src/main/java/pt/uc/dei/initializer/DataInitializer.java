package pt.uc.dei.initializer;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.services.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Orchestrates system initialization during application startup.
 * <p>
 * Executes in sequence:
 * 1. Admin user creation
 * 2. System configuration setup
 *
 * @Singleton @Startup Ensures immediate execution on deployment
 */
@Singleton
@Startup
public class DataInitializer {
    private final Logger LOGGER = LogManager.getLogger(DataInitializer.class);
    @EJB
    private UserInitializer userInitializer;
    @EJB
    private ConfigurationInitializer configurationInitializer;

    /**
     * Triggers initialization chain during application startup.
     */
    @PostConstruct
    public void initializeSystemData() {
        Path uploadDir = FileService.getAvatarStoragePath();
        try {
            Files.createDirectories(uploadDir);  // Auto-creates if missing
        }
        catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        userInitializer.initializeAdminUser();
        configurationInitializer.initializeConfiguration();
    }
}