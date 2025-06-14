package pt.uc.dei.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pt.uc.dei.dtos.ConfigurationDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.services.ConfigurationService;
import pt.uc.dei.services.UserService;

import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private JWTUtil jwtUtil;

    @Inject
    private UserService userService;

    @Inject
    private ConfigurationService configurationService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod(); // Get the HTTP method

        System.out.println("Request: " + method + " " + path);

        // Define paths that should always be skipped, regardless of method
        Set<String> generalSkippedPaths = Set.of("/login", "/logout", "/public", "/activate");

        // Define method-specific skipped paths
        Map<String, Set<String>> methodSkippedPaths = Map.of(
                "POST", Set.of("/user", "/activate", "/auth", "/auth/login")
        );

        // Check if the path is globally skipped
        if (generalSkippedPaths.contains(path)) {
            return;
        }

        // Check if the path is skipped for its specific method
        if (methodSkippedPaths.containsKey(method) && methodSkippedPaths.get(method).contains(path)) {
            return;
        }


        Cookie jwtCookie = requestContext.getCookies().get("jwt");

        if (jwtCookie == null || jwtCookie.getValue().isEmpty()) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Missing token");
            return;
        }

        try {
            Claims claims = jwtUtil.validateToken(jwtCookie.getValue());

            if (claims.getExpiration().before(new Date())) {
                abort(requestContext, Response.Status.UNAUTHORIZED, "Token expired");
                return;
            }

            String email = claims.getSubject();
            UserResponseDTO user = userService.getSelfInformation(email);
            if (user == null) {
                abort(requestContext, Response.Status.UNAUTHORIZED, "User not found");
                return;
            }

            // Store user and role info for downstream use (e.g., in endpoints or role filters)
            requestContext.setProperty("user", user);
            requestContext.setProperty("isAdmin", user.getAdmin());
            requestContext.setProperty("isManager", user.getManager());

            // ⏳ Check if the token is about to expire (e.g., less than 5 minutes left)
            long timeLeft = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (timeLeft < 5 * 60 * 1000) {
                ConfigurationDTO configuration = configurationService.getLatestConfiguration();
                String newToken = jwtUtil.generateToken(user);
                NewCookie newCookie = new NewCookie("jwt", newToken, "/", null, null, (configuration.getLoginTime() * 60), true); // 1 hour
                requestContext.setProperty("newCookie", newCookie);
                requestContext.setProperty("newExpiration", jwtUtil.getExpiration(newToken));
            }

        } catch (JwtException e) {
            abort(requestContext, Response.Status.FORBIDDEN, "Invalid token");
        }
    }

    private void abort(ContainerRequestContext context, Response.Status status, String message) {
        context.abortWith(Response.status(status)
                .entity(new ApiResponse(false, message, "authError", null))
                .build());
    }
}