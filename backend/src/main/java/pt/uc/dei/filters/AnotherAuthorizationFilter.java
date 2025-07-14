package pt.uc.dei.filters;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.uc.dei.annotations.AnotherOnly;
import pt.uc.dei.annotations.SelfOrAdminOnly;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.utils.ApiResponse;

/**
 * JAX-RS filter that denies access if the authenticated user attempts to access their own resource.
 * <p>
 * This filter checks the path parameter 'id' against the authenticated user's ID. If the user attempts
 * to access a resource with an ID matching their own, the request is denied with a 403 Forbidden response.
 * This is the inverse of a typical self-or-admin filter: it blocks self-access.
 */
@AnotherOnly
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AnotherAuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    /**
     * Filters requests to deny access if the user attempts to access their own resource.
     *
     * @param requestContext the request context containing user and path information
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        UserResponseDTO user = (UserResponseDTO) requestContext.getProperty("user");

        // Get the 'id' from the path (e.g. /users/{id})
        String idParam = requestContext.getUriInfo().getPathParameters().getFirst("id");

        if (idParam != null && user != null) {
            Long pathId = Long.parseLong(idParam);
            if (user.getId().equals(pathId)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity(new ApiResponse(false, "Same id access", "errorAccessDenied", null))
                        .build());
            }
        }
    }
}