package pt.uc.dei.utils;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.ext.Provider;

/**
 * JAX-RS response filter to refresh authentication cookies unless logging out.
 * Adds a new cookie to the response if present in the request context.
 */
@Provider
public class RefreshCookieResponseFilter implements ContainerResponseFilter {
    /**
     * Adds a refreshed cookie to the response headers unless the logout endpoint is called.
     *
     * @param requestContext the request context
     * @param responseContext the response context
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // Skip refresh if the logout endpoint is called.
        String path = requestContext.getUriInfo().getPath();
        if (path != null && path.contains("logout")) {
            return;
        }
        NewCookie newCookie = (NewCookie) requestContext.getProperty("newCookie");
        if (newCookie != null) {
            responseContext.getHeaders().add("Set-Cookie", newCookie.toString());
        }
    }
}