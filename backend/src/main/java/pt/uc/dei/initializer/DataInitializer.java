package pt.uc.dei.initializer;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import pt.uc.dei.services.AvatarFileService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Orchestrates system initialization during application startup.
 * <p>
 * Executes in sequence:
 * <ol>
 *   <li>Admin user creation</li>
 *   <li>System configuration setup</li>
 * </ol>
 *
 * Annotated with <b>@Singleton</b> and <b>@Startup</b> to ensure immediate execution on deployment.
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
        Path uploadDir = AvatarFileService.getAvatarStoragePath();
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