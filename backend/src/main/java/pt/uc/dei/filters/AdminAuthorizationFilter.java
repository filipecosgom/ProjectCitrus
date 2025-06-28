package pt.uc.dei.filters;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.uc.dei.annotations.AdminOnly;
import pt.uc.dei.utils.ApiResponse;

/**
 * JAX-RS filter that restricts access to admin users only.
 * Checks user properties to determine admin access rights.
 */
@AdminOnly
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AdminAuthorizationFilter implements ContainerRequestFilter {
    /**
     * Filters requests to ensure only admin users can access the resource.
     *
     * @param requestContext the request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        Boolean isAdmin = (Boolean) requestContext.getProperty("userIsAdmin");
        if (!Boolean.TRUE.equals(isAdmin)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity(new ApiResponse(false, "Admin access required", "accessDenied", null))
                    .build());
        }
    }
}

