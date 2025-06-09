package pt.uc.dei.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

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

        // Lista de origins permitidos
        Set<String> allowedOrigins = Set.of(
            "https://localhost:3000",
            "https://127.0.0.1:5502"
        );

        // Só adiciona o header se o origin for permitido
        if (origin != null && allowedOrigins.contains(origin)) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().add("Vary", "Origin"); // Importante para proxies/caches
        }

        // Métodos e headers permitidos
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, token, password, username");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // Responde imediatamente a preflight requests
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            responseContext.setStatus(200);
        }
    }
}