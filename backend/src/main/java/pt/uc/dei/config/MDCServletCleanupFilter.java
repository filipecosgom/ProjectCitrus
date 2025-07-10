package pt.uc.dei.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;

/**
 * Cleans up ThreadContext after request has been processed.
 */
@WebFilter("/*")  // Make sure this matches all paths
public class MDCServletCleanupFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            ThreadContext.clearMap();  // Safe cleanup after everything's done
        }
    }
}

