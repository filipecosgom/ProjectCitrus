package pt.uc.dei.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Implements a request filter for logging incoming HTTP requests.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Logger instance for recording request details.
     */
    private static final Logger LOGGER = LogManager.getLogger(LoggingFilter.class);

    /**
     * Provides access to the HTTP servlet request.
     */
    @Context
    private HttpServletRequest request;

    /**
     * Filters incoming HTTP requests and logs relevant request details.
     *
     * @param requestContext The request context containing request details.
     * @throws IOException If an error occurs while processing the request.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String clientIP = getClientIP();
        String method = requestContext.getMethod();
        String endpoint = requestContext.getUriInfo().getPath();
        String userAgent = request.getHeader("User-Agent");

        // Store client IP in Log4j2 MDC for tracking purposes
        ThreadContext.put("clientIP", clientIP);

        LOGGER.info("IP: {} | {} {} | Agent: {}", clientIP, method, endpoint, userAgent);
    }

    /**
     * Clears the ThreadContext after the response is sent.
     *
     * @param requestContext  The request context containing request details.
     * @param responseContext The response context containing response details.
     * @throws IOException If an error occurs while processing the response.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Clear ThreadContext after request processing
        ThreadContext.clearMap();
    }

    /**
     * Retrieves the client IP address from HTTP headers.
     * Checks various headers commonly used by proxies and load balancers.
     *
     * @return The client's IP address.
     */
    public String getClientIP() {
        String[] headersToCheck = {
                "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headersToCheck) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}