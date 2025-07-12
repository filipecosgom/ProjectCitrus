package pt.uc.dei.filters;

import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.uc.dei.annotations.ManagerOfUser;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;

/**
 * JAX-RS filter that allows access only to the direct manager of the user specified by the endpoint id.
 * Checks the path parameter 'id' against the authenticated user's ID and verifies manager relationship.
 */
@ManagerOfUser
@Provider
@Priority(Priorities.AUTHORIZATION)
public class ManagerOfUserFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @EJB
    private UserService userService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        UserResponseDTO requester = (UserResponseDTO) requestContext.getProperty("user");
        String idParam = requestContext.getUriInfo().getPathParameters().getFirst("id");

        if (idParam != null && requester != null) {
            try {
                Long userId = Long.parseLong(idParam);
                Long managerId = requester.getId();
                boolean isManager = userService.checkIfManagerOfUser(userId, managerId);
                if (!isManager) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity(new ApiResponse(false, "Access denied: not direct manager of user.", "errorAccessDenied", null))
                        .build());
                }
            } catch (NumberFormatException e) {
                requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid user id parameter.", "errorInvalidId", null))
                    .build());
            }
        }
    }
}
