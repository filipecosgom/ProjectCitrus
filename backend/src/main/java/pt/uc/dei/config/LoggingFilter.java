package pt.uc.dei.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implements a request filter for logging incoming HTTP requests.
 * Captures essential details such as client IP, request method, endpoint, and user agent.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter {

    /**
     * Logger instance for recording request details.
     */
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

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

        LOGGER.info(String.format(
                "IP: %s | %s %s | Agent: %s",
                clientIP,
                method,
                endpoint,
                userAgent
        ));

        // Clear MDC after request processing
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