package pt.uc.dei.controllers;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import pt.uc.dei.dtos.ActivationTokenDTO;
import pt.uc.dei.dtos.ConfigurationDTO;
import pt.uc.dei.dtos.LoginDTO;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.services.ConfigurationService;
import pt.uc.dei.services.EmailService;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import jakarta.inject.Inject;
import pt.uc.dei.utils.ApiResponse;

import java.util.Map;

/**
 * REST controller for handling authentication requests.
 * <p>
 * - Provides an endpoint for user login.
 * - Uses CDI dependency injection to access services.
 * - Returns a JWT token upon successful authentication.
 * </p>
 */
@Path("/auth") // Defines the base path for authentication endpoints
public class AuthenticationController {

    /** Logger for tracking authentication requests */
    private final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);

    /** Injected UserService to handle login operations */
    @Inject
    private UserService userService;

    /** Injected TokenService for additional token-related operations */
    @Inject
    private TokenService tokenService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    EmailService emailService;

    /**
     * Handles user login and returns a JWT authentication token.
     *
     * @param user The login request containing email and password.
     * @return HTTP 200 (OK) with a JWT token if authentication is successful,
     *         otherwise HTTP 401 (Unauthorized) if login fails.
     */
    @POST
    @Path("/login") // Defines the login endpoint
    @Consumes(MediaType.APPLICATION_JSON) // Accepts JSON payload
    @Produces(MediaType.APPLICATION_JSON) // Ensures response is JSON
    public Response login(LoginDTO user) {
        // Attempt to authenticate user and generate JWT token
        String token = userService.loginUser(user);

        // If authentication fails, return structured error response
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Invalid credentials", "errorInvalidCredentials", null))
                    .build();
        }

        // Retrieve configuration settings
        ConfigurationDTO configuration = configurationService.getLatestConfiguration();

        // Prepare the response with JWT in headers and structured body
        Response.ResponseBuilder response = Response.ok(new ApiResponse(true, "Login successful", null, Map.of("token", token)));

        response.header("Set-Cookie",
                "jwt=" + token +
                        "; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=" + (configuration.getLoginTime() * 60)
        );

        return response.build();
    }

    @POST
    @Path("/password-reset")
    @Consumes(MediaType.APPLICATION_JSON) // Accepts JSON payload
    @Produces(MediaType.APPLICATION_JSON) // Ensures response is JSON
    public Response requestPasswordReset(JsonObject emailJSON) {
        String email = emailJSON.getString("email");

        if (email == null || email.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid request: missing email", "errorMissingEmail", null))
                    .build();
        }

        try {
            String token = tokenService.createNewPasswordResetToken(email);

            if (token == null) {
                LOGGER.error("Invalid reset token request for {}", email);
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Unauthorized request", "errorInvalidResetToken", null))
                        .build();
            }

            emailService.sendPassworResetEmail(email, token);

            return Response.status(Response.Status.CREATED)
                    .entity(new ApiResponse(true, "Password reset token generated successfully", null, Map.of("token", token)))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Password reset error for {}: {}", email, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Password reset failed", "errorServerIssue", null))
                    .build();
        }
    }
}