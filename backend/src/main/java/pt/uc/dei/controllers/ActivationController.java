package pt.uc.dei.controllers;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.ActivationTokenDTO;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.repositories.ActivationTokenRepository;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;

/**
 * REST controller for handling account activation requests.
 * <p>
 * This endpoint processes activation tokens sent to users via email and manages
 * the account activation workflow including:
 * <ul>
 *   <li>Token validation</li>
 *   <li>Expired token renewal</li>
 *   <li>Temporary to permanent account conversion</li>
 *   <li>Cleanup of temporary registration data</li>
 * </ul>
 *
 * @Path("/activate") Root path for activation endpoints
 */
@Path("/activate")
public class ActivationController {
    /**
     * Logger instance for tracking activation process events.
     */
    private static final Logger LOGGER = LogManager.getLogger(ActivationController.class);

    /**
     * Injected service for token-related operations.
     */
    @EJB
    private TokenService tokenService;

    /**
     * Injected service for user-related operations.
     */
    @EJB
    private UserService userService;

    /**
     * Activates a user account using the provided activation token.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Validates the token exists and is associated with a temporary user</li>
     *   <li>Checks token expiration (renews if expired)</li>
     *   <li>Converts temporary account to permanent</li>
     *   <li>Cleans up temporary data</li>
     * </ol>
     *
     * @param token Activation token from email link (in header)
     * @return HTTP Response with appropriate status:
     *         - 202 (Accepted) for successful activation
     *         - 401 (Unauthorized) for invalid tokens
     *         - 409 (Conflict) with new token if original expired
     *         - 500 (Internal Server Error) for processing failures
     *
     * @HTTP 401 If no temporary user is associated with the token
     * @HTTP 409 If token was expired (includes new token in response)
     * @HTTP 500 If activation or cleanup processes fail
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response activateAccount(@HeaderParam("token") String token) {
        try {
            // Step 1: Validate token and get associated user
            ActivationTokenDTO activationToken = new ActivationTokenDTO();
            activationToken.setTokenValue(token);
            TemporaryUserDTO userToActivate = tokenService.getTemporaryUserFromActivationToken(activationToken);

            if (userToActivate == null) {
                LOGGER.warn("Activation attempt with invalid token: {}", token);
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            // Step 2: Check token expiration
            activationToken = tokenService.getActivationTokenByValue(activationToken);
            if (tokenService.isTokenExpired(activationToken)) {
                String newToken = tokenService.renewToken(userToActivate, activationToken);
                if (newToken != null) {
                    LOGGER.warn("Expired token used for {} - New token generated", userToActivate.getEmail());
                    return Response.status(Response.Status.CONFLICT).entity(newToken).build();
                }
                LOGGER.error("Token renewal failed for {}", userToActivate.getEmail());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }


            // Step 3: Activate user
            if (!userService.activateUser(userToActivate)) {
                LOGGER.error("Account activation failed for {}", userToActivate.getEmail());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            // Step 4: Cleanup
            if (!userService.deleteTemporaryUserInformation(userToActivate)) {
                LOGGER.error("Temporary data cleanup failed for {}", userToActivate.getEmail());
                // Still return success as account was activated
            }

            LOGGER.info("Account successfully activated for {}", userToActivate.getEmail());
            return Response.status(Response.Status.ACCEPTED).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error during account activation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}