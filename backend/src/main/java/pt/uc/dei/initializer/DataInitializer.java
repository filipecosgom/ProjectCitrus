package pt.uc.dei.initializer;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
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
    @EJB
    private UserInitializer userInitializer;
    @EJB
    private ConfigurationInitializer configurationInitializer;

    /**
     * Triggers initialization chain during application startup.
     */
    @PostConstruct
    public void initializeSystemData() {
        userInitializer.initializeAdminUser();
        configurationInitializer.initializeConfiguration();
    }
}