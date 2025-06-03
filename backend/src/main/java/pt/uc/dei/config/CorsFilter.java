package pt.uc.dei.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements a Cross-Origin Resource Sharing (CORS) filter for HTTP responses.
 * Ensures that requests from allowed origins can access the application's resources.
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    /**
     * Logger for logging CORS-related events.
     */
    private static final Logger LOGGER = LogManager.getLogger(CorsFilter.class);

    /**
     * Adds necessary CORS headers to HTTP responses.
     *
     * @param requestContext  The HTTP request context.
     * @param responseContext The HTTP response context.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // Retrieve the request's origin header
        String origin = requestContext.getHeaderString("Origin");

        // Allow requests from specified origins
        if ("https://127.0.0.1:5502".equals(origin) || "https://localhost:3000".equals(origin)) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
        }

        // Define allowed HTTP methods
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");

        // Define allowed HTTP headers
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, token, password, username");

        // Allow credentials to be included in requests
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // Handle preflight OPTIONS requests explicitly
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(200);
        }
    }
}