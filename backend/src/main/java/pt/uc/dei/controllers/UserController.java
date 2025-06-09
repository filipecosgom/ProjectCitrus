package pt.uc.dei.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.services.EmailService;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;

import java.util.Date;
import java.util.Map;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid TemporaryUserDTO temporaryUserDTO, @HeaderParam("lang") String language) {
        if (userService.findIfUserExists(temporaryUserDTO.getEmail())) {
            LOGGER.info("Duplicate email attempt: {}", temporaryUserDTO.getEmail());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, "Email already registered", "errorDuplicateEntry", null))
                    .build();
        }
        try {
            String activationToken = userService.registerUser(temporaryUserDTO);
            if (activationToken == null) {
                LOGGER.error("Token generation failed for {}", temporaryUserDTO.getEmail());
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ApiResponse(false, "Activation token failed", "errorActivationFailed", null))
                        .build();
            }
            // Send email and return response
            emailService.sendActivationEmail(temporaryUserDTO.getEmail(), activationToken, language);
            ApiResponse response = new ApiResponse(true, "Account created", null, Map.of("token", activationToken));
            System.out.println("Response: " + new ObjectMapper().writeValueAsString(response)); // Debug serialization
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, e.getMessage(), "errorInvalidData", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Registration error for {}: {}", temporaryUserDTO.getEmail(), e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Registration failed", "errorServerIssue", null))
                    .build();
        }
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserData(@Context HttpHeaders headers) {
        Map<String, Cookie> cookies = headers.getCookies();
        Cookie jwtCookie = (cookies != null) ? cookies.get("jwt") : null;
        if (jwtCookie == null || jwtCookie.getValue().isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Missing token", "errorMissingToken", null))
                    .build();
        }
        try {
            Claims claims = JWTUtil.validateToken(jwtCookie.getValue());
            // Check for token expiration
            if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Token expired", "errorTokenExpired", null))
                        .build();
            }
            // Construct UserDTO from JWT claims
            UserDTO user = new UserDTO(
                    claims.getSubject(),
                    Boolean.TRUE.equals(claims.get("isAdmin")),
                    Boolean.TRUE.equals(claims.get("isManager"))
            );
            return Response.ok(new ApiResponse(true, "User data retrieved", null, user)).build();
        } catch (JwtException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ApiResponse(false, "Invalid token", "errorInvalidToken", null))
                    .build();
        }
    }
}