package pt.uc.dei.filters;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.annotations.AdminSelfOrManager;

/**
 * JAX-RS filter that allows access only to the user themselves or an admin.
 * Checks the path parameter 'id' against the authenticated user's ID and admin status.
 */
@AdminSelfOrManager
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AdminSelfOrManagerFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    /**
     * Filters requests to ensure only the user themselves or an admin can access the resource.
     *
     * @param requestContext the request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        UserResponseDTO user = (UserResponseDTO) requestContext.getProperty("user");
        Boolean isAdmin = (Boolean) requestContext.getProperty("userIsAdmin");

        // Get the 'id' from the path (e.g. /users/{id})
        String idParam = requestContext.getUriInfo().getPathParameters().getFirst("id");

        if (idParam != null && user != null) {
            Long pathId = Long.parseLong(idParam);
            if (!user.getId().equals(pathId) && !Boolean.TRUE.equals(isAdmin)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity(new ApiResponse(false, "Access denied", "errorAccessDenied", null))
                        .build());
            }
        }
    }
}