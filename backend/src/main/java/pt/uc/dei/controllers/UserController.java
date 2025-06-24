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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import pt.uc.dei.dtos.*;
import pt.uc.dei.enums.*;
import pt.uc.dei.services.*;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.SearchUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

    @Context Request request;

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
            emailService.sendActivationEmail(temporaryUserDTO.getEmail(), codes.get("token"), codes.get("secretKey"), language);
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, UpdateUserDTO updatedUserDTO) {
        try {
            boolean updated = userService.updateUser(id, updatedUserDTO);
            if (updated) {
                // Fetch the updated user to return in the response.
                UserDTO updatedUser = userService.getUser(id);
                return Response.ok(
                        new ApiResponse(true, "User updated successfully", "successUserUpdated", updatedUser)
                ).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "User not found with id: " + id, "errorUserNotFound", null))
                        .build();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update user with id: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Unexpected error updating user.", "errorInternal", null))
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAvatar(@PathParam("id") Long id, @MultipartForm FileUploadDTO form) {
        InputStream avatarStream = form.getFileStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            avatarStream.transferTo(baos);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Unsupported file type", "errorInvalidType", null))
                    .build();
        }
        byte[] fileBytes = baos.toByteArray();
        InputStream forMimeType = new ByteArrayInputStream(fileBytes);
        InputStream forSaving = new ByteArrayInputStream(fileBytes);
        String filename = FileService.getFilename(id, fileBytes);
        if (!FileService.isValidMimeType(forMimeType)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Unsupported file type", "errorInvalidType", null))
                    .build();
        }
        FileService.removeExistingFiles(id);
        boolean saved = FileService.saveFileWithSizeLimit(forSaving, filename);
        if (!saved) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "File too large", "errorFileTooLarge", null))
                    .build();
        }
        UpdateUserDTO user = new UpdateUserDTO();
        user.setHasAvatar(true);
        userService.updateUser(id, user);
        return Response.status(Response.Status.OK)
                .entity(new ApiResponse(true, "File uploaded successfully", "successFileUploaded", filename))
                .build();
    }

    @GET
    @Path("/{id}/avatar")
    @Produces({MediaType.APPLICATION_JSON, "image/jpeg", "image/png", "image/webp"})
    public Response getAvatar(
            @PathParam("id") Long id,
            @Context Request request,
            @Context HttpHeaders headers) {

        try {
            java.nio.file.Path avatarPath = FileService.resolveAvatarPath(id);
            if (avatarPath == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new ApiResponse(false, "Avatar not found", "avatarNotFound", null))
                        .build();
            }

            // Get cache metadata
            FileService.CacheData cacheData = FileService.getCacheData(avatarPath);

            // Create ETag
            EntityTag etag = new EntityTag(cacheData.lastModified + "-" + cacheData.fileSize);

            // Check preconditions
            Response.ResponseBuilder builder = request.evaluatePreconditions(
                    new Date(cacheData.lastModified),
                    etag
            );

            if (builder != null) {
                return builder.build();
            }

            // Check if client accepts JSON (for error cases)
            boolean acceptsJson = headers.getAcceptableMediaTypes().stream()
                    .anyMatch(m -> m.isCompatible(MediaType.APPLICATION_JSON_TYPE));

            // Build streaming response
            StreamingOutput stream = output -> {
                try (InputStream in = Files.newInputStream(avatarPath)) {
                    in.transferTo(output);
                } catch (IOException e) {
                    if (acceptsJson) {
                        // If streaming fails and client accepts JSON
                        output.write(new ObjectMapper().writeValueAsBytes(
                                new ApiResponse(false, "Stream error", "streamError", null)
                        ));
                    }
                }
            };
            return Response.ok(stream)
                    .type(cacheData.mimeType)
                    .header("Cache-Control", "public, max-age=86400")
                    .header("ETag", etag.toString())
                    .lastModified(new Date(cacheData.lastModified))
                    .build();

        } catch (IOException e) {
            return Response.serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ApiResponse(false, "Failed to load avatar", "avatarLoadError", null))
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("id") Long id,
                             @QueryParam("email") String email,
                             @QueryParam("name") String name,
                             @QueryParam("phone") String phone,
                             @QueryParam("accountState") String accountStateStr,
                             @QueryParam("role") String roleStr,
                             @QueryParam("office") String officeStr,
                             @QueryParam("parameter") @DefaultValue("name") String parameterStr,
                             @QueryParam("order") @DefaultValue("ASCENDING") String orderStr,
                             @QueryParam("offset") @DefaultValue("0") int offset,
                             @QueryParam("limit") @DefaultValue("10") int limit) {
        AccountState accountState = accountStateStr != null ? AccountState.valueOf(SearchUtils.normalizeString(accountStateStr)) : null;
        Office office = officeStr != null ? Office.fromFieldName(SearchUtils.normalizeString(officeStr)) : null;
        Parameter parameter = Parameter.fromFieldName(SearchUtils.normalizeString(parameterStr));
        Order order = Order.fromFieldName(SearchUtils.normalizeString(orderStr));

        Map<String, Object> userData = userService.getUsers(id, email, name, phone,
                accountState, roleStr, office,
                parameter, order, offset, limit);

        if (userData.get("users") == null || ((List<?>) userData.get("users")).isEmpty()) {
            return Response.status(404).entity(new ApiResponse(false, "No users found", "NOT_FOUND", null)).build();
        }
        return Response.ok(new ApiResponse(true, "Users retrieved successfully", null, userData)).build();
    }
}