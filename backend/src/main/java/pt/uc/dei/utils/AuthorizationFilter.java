package pt.uc.dei.utils;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@PreMatching // Runs before request reaches resource method
@Provider
public class AuthorizationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        Boolean isAdmin = (Boolean) requestContext.getProperty("isAdmin");
        Boolean isManager = (Boolean) requestContext.getProperty("isManager");

        if (path.startsWith("admin") && !Boolean.TRUE.equals(isAdmin)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity(new ApiResponse(false, "Access denied", "errorAccessDenied", null))
                    .build());
        }

        if (path.startsWith("manager") && !Boolean.TRUE.equals(isManager)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity(new ApiResponse(false, "Access denied", "errorAccessDenied", null))
                    .build());
        }
    }
}
