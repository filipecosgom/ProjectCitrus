package pt.uc.dei.utils;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.uc.dei.annotations.ManagerOnly;

@ManagerOnly
@Provider
@Priority(Priorities.AUTHORIZATION)
public class ManagerAuthorizationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Boolean isManager = (Boolean) requestContext.getProperty("userIsManager");
        Boolean isAdmin = (Boolean) requestContext.getProperty("userIsAdmin");

        // Allow if user is a manager or an admin
        if (!Boolean.TRUE.equals(isManager) && !Boolean.TRUE.equals(isAdmin)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity(new ApiResponse(false, "Manager or admin access required", "accessDenied", null))
                    .build());
        }
    }
}