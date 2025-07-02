package pt.uc.dei.filters;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.uc.dei.annotations.DirectManagerOnly;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.UserRepository;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;

import java.io.IOException;

@DirectManagerOnly
@Provider
@Priority(Priorities.AUTHORIZATION)
public class DirectManagerAuthorizationFilter implements ContainerRequestFilter {

    @Inject
    UserRepository userRepository;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Long requesterId = (Long) requestContext.getProperty("userId");
        Long targetUserId = extractTargetUserIdFromPath(requestContext);

        if (requesterId == null || targetUserId == null) {
            abort(requestContext, "Missing user or target ID");
            return;
        }
        boolean isManagerOf = checkIfManagerOf(requesterId, targetUserId);
        if (!isManagerOf) {
            abort(requestContext, "Access denied: not the target user's manager");
        }
    }

    private void abort(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity(new ApiResponse(false, message, "accessDenied", null))
                .build());
    }

    private Long extractTargetUserIdFromPath(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();
        // For example, if the path is /users/42/appraisal
        try {
            String[] segments = path.split("/");
            int userIdIndex = -1;
            for (int i = 0; i < segments.length; i++) {
                if ("users".equals(segments[i]) && i + 1 < segments.length) {
                    userIdIndex = i + 1;
                    break;
                }
            }
            if (userIdIndex != -1) {
                return Long.parseLong(segments[userIdIndex]);
            }
        } catch (Exception e) {
            // log warning if needed
        }
        return null;
    }

    private boolean checkIfManagerOf(Long managerId, Long userId) {
        UserEntity user = userRepository.findUserById(userId);
        return managerId.equals(user.getManagerUser().getId());
    }
}