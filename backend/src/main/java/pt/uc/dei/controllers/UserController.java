package pt.uc.dei.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.inject.Inject;
import jakarta.persistence.EnumType;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.TemporaryUserDTO;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.enums.*;
import pt.uc.dei.services.AuthenticationService;
import pt.uc.dei.services.EmailService;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;

import java.time.LocalDate;
import java.util.*;

/**
 * Handles user registration endpoints.
 */
@Path("/users")
public class UserController {
    /**
     * Logger for user registration events
     */
    private final Logger LOGGER = LogManager.getLogger(UserController.class);

    /**
     * Handles user registration logic
     */
    @Inject
    UserService userService;

    /**
     * Manages activation token generation
     */
    @Inject
    AuthenticationService authenticationService;

    /**
     * Sends activation emails
     */
    @Inject
    EmailService emailService;

    /**
     * Registers a new user and sends activation email.
     *
     * @param temporaryUserDTO Contains email and password for registration
     * @return HTTP response:
     * - 201 (Created) with token if successful
     * - 400 (Bad Request) for invalid data
     * - 409 (Conflict) if email exists
     * - 500 (Error) for server failures
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid TemporaryUserDTO temporaryUserDTO, @HeaderParam("Accept-Language") String language) {
        if (userService.findIfUserExists(temporaryUserDTO.getEmail())) {
            LOGGER.info("Duplicate email attempt: {}", temporaryUserDTO.getEmail());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, "Email already registered", "errorDuplicateEntry", null))
                    .build();
        }
        try {
            Map<String, String> codes = userService.registerUser(temporaryUserDTO);
            if (codes.get("token") == null) {
                LOGGER.error("Token generation failed for {}", temporaryUserDTO.getEmail());
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(new ApiResponse(false, "Activation token failed", "errorActivationFailed", null))
                        .build();
            }
            if (codes.get("secretKey") == null) {
                LOGGER.error("Invalid authentication code request for {}", temporaryUserDTO.getEmail());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ApiResponse(false, "Operation error", "errorNoAuthCode", null))
                        .build();
            }
            // Send email and return response
            emailService.sendActivationEmail(temporaryUserDTO.getEmail(), codes.get("token"), codes.get("authenticationCode"), language);
            ApiResponse response = new ApiResponse(true, "Account created", null, codes);
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
            if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Token expired", "errorTokenExpired", null))
                        .build();
            }
            UserResponseDTO user = authenticationService.getSelfInformation(Long.parseLong(claims.getSubject()));
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponse(false,
                                "User not found",
                                "errorUserNotFound", null))
                        .build();
            }
            return Response.ok(new ApiResponse(true,
                    "User data retrieved",
                    null,
                    Map.of(
                            "user", user,
                            "tokenExpiration", claims.getExpiration().getTime())
            )).build();
        } catch (JwtException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ApiResponse(false, "Invalid token", "errorInvalidToken", null))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        try {
            UserDTO user = userService.getUser(id);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "User not found with id: " + id, "errorUserNotFound", null))
                        .build();
            }

            return Response.ok(new ApiResponse(true, "User retrieved successfully", "successUserRetrieved", user))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Failed to fetch user by id: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Unexpected error fetching user.", "errorInternal", null))
                    .build();
        }
    }

    @PATCH
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, UserDTO updatedUser) {
        try {
            UserDTO user = userService.getUser(id);

            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "User not found with id: " + id, "errorUserNotFound", null))
                        .build();
            }

            return Response.ok(new ApiResponse(true, "User retrieved successfully", "successUserRetrieved", user))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Failed to fetch user by id: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Unexpected error fetching user.", "errorInternal", null))
                    .build();
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("id") Long id,
                             @QueryParam("email") String email,
                             @QueryParam("name") String name,
                             @QueryParam("surname") String surname,
                             @QueryParam("phone") String phone,
                             @QueryParam("accountState") String accountStateStr,
                             @QueryParam("role") String roleStr,
                             @QueryParam("office") String officeStr,
                             @QueryParam("parameter") @DefaultValue("name") String parameterStr,
                             @QueryParam("order") @DefaultValue("ASCENDING") String orderStr,
                             @QueryParam("offset") @DefaultValue("0") int offset,
                             @QueryParam("limit") @DefaultValue("10") int limit) {
        AccountState accountState = accountStateStr != null ? AccountState.valueOf(accountStateStr) : null;
        Role role = Role.fromFieldName(roleStr);
        Office office = Office.fromFieldName(officeStr);
        Parameter parameter = Parameter.fromFieldName(parameterStr);
        Order order = Order.fromFieldName(orderStr);

        Map<String, Object> userData = userService.getUsers(id, email, name, surname, phone,
                accountState, role, office,
                parameter, order, offset, limit);

        if (userData.get("users") == null || ((List<?>) userData.get("users")).isEmpty()) {
            return Response.status(404).entity(new ApiResponse(false, "No users found", "NOT_FOUND", null)).build();
        }
        return Response.ok(new ApiResponse(true, "Users retrieved successfully", null, userData)).build();
    }
}