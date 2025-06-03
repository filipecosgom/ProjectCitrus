package pt.uc.dei.config;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration class for the JAX-RS application.
 * Defines the base URI path for RESTful web services.
 */
@ApplicationPath("/rest")
public class ApplicationConfig extends Application {

    /**
     * The URL of the frontend application.
     * Used to reference frontend resources or for cross-origin requests.
     */
    public static final String FRONTEND_URL = "http://localhost:3000";
}