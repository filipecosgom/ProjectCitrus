package pt.uc.dei.controllers;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import pt.uc.dei.dtos.ActivationTokenDTO;
import pt.uc.dei.dtos.LoginDTO;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import jakarta.inject.Inject;

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
    private final Logger logger = LogManager.getLogger(AuthenticationController.class);

    /** Injected UserService to handle login operations */
    @Inject
    private UserService userService;

    /** Injected TokenService for additional token-related operations */
    @Inject
    private TokenService tokenService;

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
    public Response login(LoginDTO user) {
        // Attempt to authenticate user and generate JWT token
        String token = userService.loginUser(user);

        // If authentication fails, return HTTP 401 (Unauthorized)
        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // If authentication is successful, return HTTP 200 with the token
        return Response.ok(token).build();
    }
}