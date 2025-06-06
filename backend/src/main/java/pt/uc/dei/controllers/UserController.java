package pt.uc.dei.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.config.EmailConfig;
import pt.uc.dei.config.LoggingFilter;
import pt.uc.dei.dtos.ActivationTokenDTO;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.services.EmailService;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.JWTUtil;

/**
 * Handles user registration endpoints.
 */
@Path("/user")
public class UserController {
    /** Logger for user registration events */
    private final Logger LOGGER = LogManager.getLogger(UserController.class);

    /** Handles user registration logic */
    @Inject
    UserService userService;

    /** Manages activation token generation */
    @Inject
    TokenService tokenService;

    /** Sends activation emails */
    @Inject
    EmailService emailService;

    /**
     * Registers a new user and sends activation email.
     *
     * @param temporaryUserDTO Contains email and password for registration
     * @return HTTP response:
     *         - 201 (Created) with token if successful
     *         - 400 (Bad Request) for invalid data
     *         - 409 (Conflict) if email exists
     *         - 500 (Error) for server failures
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid TemporaryUserDTO temporaryUserDTO) {
        // Check for existing user
        if (userService.findIfUserExists(temporaryUserDTO.getEmail())) {
            LOGGER.info("Duplicate email attempt: {}", temporaryUserDTO.getEmail());
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Email already registered\"}")
                    .build();
        }
        try {
            // Process registration
            String activationToken = userService.registerUser(temporaryUserDTO);
            if (activationToken == null) {
                LOGGER.error("Token generation failed for {}", temporaryUserDTO.getEmail());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"Activation token failed\"}")
                        .build();
            }
            // Send email and return response
            emailService.sendActivationEmail(temporaryUserDTO.getEmail(), activationToken);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"token\": \"" + activationToken + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            LOGGER.error("Registration error for {}: {}", temporaryUserDTO.getEmail(), e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Registration failed\"}")
                    .build();
        }
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserData(@Context HttpHeaders headers) {
        Cookie jwtCookie = headers.getCookies().get("jwt");
        if (jwtCookie == null || jwtCookie.getValue().isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing token").build();
        }
        try {
            Claims claims = JWTUtil.validateToken(jwtCookie.getValue());
            UserDTO user = new UserDTO(
                    claims.getSubject(),
                    Boolean.TRUE.equals(claims.get("isAdmin")),
                    Boolean.TRUE.equals(claims.get("isManager"))
            );
            return Response.ok(user).build();
        } catch (JwtException e) { // Catching JWT-specific exceptions
            return Response.status(Response.Status.FORBIDDEN).entity("Invalid token").build();
        }
    }
}