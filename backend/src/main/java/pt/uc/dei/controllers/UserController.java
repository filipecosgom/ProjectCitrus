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
import pt.uc.dei.annotations.AllowAnonymous;
import pt.uc.dei.annotations.SelfOrAdminOnly;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Handles user registration and management endpoints.
 */
@Path("/users")
public class UserController {
    /**
     * Logger for user registration and management events.
     */
    private final Logger LOGGER = LogManager.getLogger(UserController.class);

    @Inject
    UserService userService;

    @Context
    Request request;

    @Inject
    AuthenticationService authenticationService;

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
    @AllowAnonymous
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid TemporaryUserDTO temporaryUserDTO, @HeaderParam("Accept-Language") String language) {
        LOGGER.info("Registration attempt for email: {}", temporaryUserDTO.getEmail());
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
            emailService.sendActivationEmail(temporaryUserDTO.getEmail(), codes.get("token"), codes.get("secretKey"), language);
            LOGGER.info("Activation email sent to {}", temporaryUserDTO.getEmail());
            ApiResponse response = new ApiResponse(true, "Account created", null, codes);
            System.out.println("Response: " + new ObjectMapper().writeValueAsString(response)); // Debug serialization
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid registration data for {}: {}", temporaryUserDTO.getEmail(), e.getMessage());
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

    /**
     * Retrieves the authenticated user's data using the JWT cookie.
     *
     * @param headers HTTP headers containing cookies.
     * @return HTTP 200 (OK) with user data, or error response.
     */
    @GET
    @Path("/me")
    @SelfOrAdminOnly
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserData(@Context HttpHeaders headers) {
        LOGGER.info("Request to get current user data");
        Map<String, Cookie> cookies = headers.getCookies();
        Cookie jwtCookie = (cookies != null) ? cookies.get("jwt") : null;
        if (jwtCookie == null || jwtCookie.getValue().isEmpty()) {
            LOGGER.warn("Missing JWT cookie in user data request");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Missing token", "errorMissingToken", null))
                    .build();
        }
        try {
            Claims claims = JWTUtil.validateToken(jwtCookie.getValue());
            if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
                LOGGER.warn("Expired JWT token in user data request");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Token expired", "errorTokenExpired", null))
                        .build();
            }
            UserResponseDTO user = authenticationService.getSelfInformation(Long.parseLong(claims.getSubject()));
            if (user == null) {
                LOGGER.warn("User not found for subject: {}", claims.getSubject());
                return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponse(false,
                                "User not found",
                                "errorUserNotFound", null))
                        .build();
            }
            LOGGER.info("User data retrieved for user id: {}", claims.getSubject());
            return Response.ok(new ApiResponse(true,
                    "User data retrieved",
                    null,
                    Map.of(
                            "user", user,
                            "tokenExpiration", claims.getExpiration().getTime())
            )).build();
        } catch (JwtException e) {
            LOGGER.error("Invalid JWT token in user data request: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ApiResponse(false, "Invalid token", "errorInvalidToken", null))
                    .build();
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id User ID.
     * @return HTTP 200 (OK) with user data, or error response.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        LOGGER.info("Request to get user by id: {}", id);
        try {
            UserDTO user = userService.getUser(id);

            if (user == null) {
                LOGGER.warn("User not found with id: {}", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "User not found with id: " + id, "errorUserNotFound", null))
                        .build();
            }

            LOGGER.info("User retrieved successfully for id: {}", id);
            return Response.ok(new ApiResponse(true, "User retrieved successfully", "successUserRetrieved", user))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Failed to fetch user by id: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Unexpected error fetching user.", "errorInternal", null))
                    .build();
        }
    }

    /**
     * Updates a user's information.
     *
     * @param id             User ID.
     * @param updatedUserDTO DTO with updated user data.
     * @return HTTP 200 (OK) if updated, or error response.
     */
    @SelfOrAdminOnly
    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, UpdateUserDTO updatedUserDTO) {
        LOGGER.info("Update request for user id: {}", id);
        try {
            boolean updated = userService.updateUser(id, updatedUserDTO);
            if (updated) {
                UserDTO updatedUser = userService.getUser(id);
                LOGGER.info("User updated successfully for id: {}", id);
                return Response.ok(
                        new ApiResponse(true, "User updated successfully", "successUserUpdated", updatedUser)
                ).build();
            } else {
                LOGGER.warn("User not found for update with id: {}", id);
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

    /**
     * Uploads or updates a user's avatar.
     *
     * @param id   User ID.
     * @param form Multipart form containing the avatar file.
     * @return HTTP 200 (OK) if uploaded, or error response.
     */
    @SelfOrAdminOnly
    @PATCH
    @Path("/{id}/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAvatar(@PathParam("id") Long id, @MultipartForm FileUploadDTO form) {
        LOGGER.info("Avatar upload request for user id: {}", id);
        InputStream avatarStream = form.getFileStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            avatarStream.transferTo(baos);
        } catch (Exception e) {
            LOGGER.error("Failed to read avatar file for user id: {}", id, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Unsupported file type", "errorInvalidType", null))
                    .build();
        }
        byte[] fileBytes = baos.toByteArray();
        InputStream forMimeType = new ByteArrayInputStream(fileBytes);
        InputStream forSaving = new ByteArrayInputStream(fileBytes);
        String filename = AvatarFileService.getFilename(id, fileBytes);
        if (!AvatarFileService.isValidMimeType(forMimeType)) {
            LOGGER.warn("Invalid mime type for avatar upload by user id: {}", id);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Unsupported file type", "errorInvalidType", null))
                    .build();
        }
        AvatarFileService.removeExistingFiles(id);
        boolean saved = AvatarFileService.saveFileWithSizeLimit(forSaving, filename);
        if (!saved) {
            LOGGER.warn("File too large for avatar upload by user id: {}", id);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "File too large", "errorFileTooLarge", null))
                    .build();
        }
        UpdateUserDTO user = new UpdateUserDTO();
        user.setHasAvatar(true);
        userService.updateUser(id, user);
        LOGGER.info("Avatar uploaded successfully for user id: {}", id);
        return Response.status(Response.Status.OK)
                .entity(new ApiResponse(true, "File uploaded successfully", "successFileUploaded", filename))
                .build();
    }

    /**
     * Retrieves a user's avatar image.
     *
     * @param id      User ID.
     * @param request HTTP request context.
     * @param headers HTTP headers.
     * @return The avatar image or error response.
     */
    @GET
    @Path("/{id}/avatar")
    @Produces({MediaType.APPLICATION_JSON, "image/jpeg", "image/png", "image/webp"})
    public Response getAvatar(
            @PathParam("id") Long id,
            @Context Request request,
            @Context HttpHeaders headers) {

        LOGGER.info("Avatar retrieval request for user id: {}", id);
        try {
            java.nio.file.Path avatarPath = AvatarFileService.resolveAvatarPath(id);
            if (avatarPath == null) {
                LOGGER.warn("Avatar not found for user id: {}", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new ApiResponse(false, "Avatar not found", "avatarNotFound", null))
                        .build();
            }

            AvatarFileService.CacheData cacheData = AvatarFileService.getCacheData(avatarPath);
            EntityTag etag = new EntityTag(cacheData.lastModified + "-" + cacheData.fileSize);

            Response.ResponseBuilder builder = request.evaluatePreconditions(
                    new Date(cacheData.lastModified),
                    etag
            );

            if (builder != null) {
                LOGGER.info("Avatar not modified for user id: {}", id);
                return builder.build();
            }

            boolean acceptsJson = headers.getAcceptableMediaTypes().stream()
                    .anyMatch(m -> m.isCompatible(MediaType.APPLICATION_JSON_TYPE));

            StreamingOutput stream = output -> {
                try (InputStream in = Files.newInputStream(avatarPath)) {
                    in.transferTo(output);
                } catch (IOException e) {
                    LOGGER.error("Streaming error for avatar of user id: {}", id, e);
                    if (acceptsJson) {
                        output.write(new ObjectMapper().writeValueAsBytes(
                                new ApiResponse(false, "Stream error", "streamError", null)
                        ));
                    }
                }
            };
            LOGGER.info("Avatar returned for user id: {}", id);
            return Response.ok(stream)
                    .type(cacheData.mimeType)
                    .header("Cache-Control", "public, max-age=86400")
                    .header("ETag", etag.toString())
                    .lastModified(new Date(cacheData.lastModified))
                    .build();

        } catch (IOException e) {
            LOGGER.error("Failed to load avatar for user id: {}", id, e);
            return Response.serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ApiResponse(false, "Failed to load avatar", "avatarLoadError", null))
                    .build();
        }
    }

    /**
     * Retrieves users with optional filters and pagination.
     *
     * @param id              User ID filter.
     * @param email           Email filter.
     * @param name            Name filter.
     * @param phone           Phone filter.
     * @param accountStateStr Account state filter.
     * @param roleStr         Role filter.
     * @param officeStr       Office filter.
     * @param parameterStr    Sort parameter.
     * @param orderStr        Sort order.
     * @param offset          Pagination offset.
     * @param limit           Pagination limit.
     * @return HTTP 200 (OK) with users or message if none found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("id") Long id,
                             @QueryParam("email") String email,
                             @QueryParam("name") String name,
                             @QueryParam("phone") String phone,
                             @QueryParam("accountState") String accountStateStr,
                             @QueryParam("role") String roleStr,
                             @QueryParam("office") String officeStr,
                             @QueryParam("isManager") Boolean userIsManager,
                             @QueryParam("isAdmin") Boolean userIsAdmin,
                             @QueryParam("isManaged") Boolean userIsManaged,
                             @QueryParam("parameter") @DefaultValue("name") String parameterStr,
                             @QueryParam("order") @DefaultValue("ASCENDING") String orderStr,
                             @QueryParam("offset") @DefaultValue("0") int offset,
                             @QueryParam("limit") @DefaultValue("10") int limit,
                             @CookieParam("jwt") String jwtToken) {
        LOGGER.info("User list request with filters: id={}, email={}, name={}, phone={}", id, email, name, phone);
        if (jwtToken == null || jwtToken.isEmpty()) {
            LOGGER.warn("Missing JWT token in user list request");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Missing token", "errorMissingToken", null))
                    .build();
        }
        if(accountStateStr != null && !accountStateStr.isEmpty()) {
            if(!JWTUtil.isUserAdmin(jwtToken)){
                LOGGER.warn("Unauthorized access to account state filter without admin privileges");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ApiResponse(false, "Forbidden", "errorForbidden", null))
                        .build();
            }
        }
        AccountState accountState = accountStateStr != null ? AccountState.valueOf(SearchUtils.normalizeString(accountStateStr)) : null;
        Office office = officeStr != null ? Office.fromFieldName(SearchUtils.normalizeString(officeStr)) : null;
        Parameter parameter = Parameter.fromFieldName(SearchUtils.normalizeString(parameterStr));
        OrderBy orderBy = OrderBy.fromFieldName(SearchUtils.normalizeString(orderStr));

        Map<String, Object> userData = userService.getUsers(id, email, name, phone,
                accountState, roleStr, office, userIsManager, userIsAdmin, userIsManaged,
                parameter, orderBy, offset, limit);

        if (userData.get("users") == null || ((List<?>) userData.get("users")).isEmpty()) {
            LOGGER.info("No users found for given filters");
            return Response.status(200).entity(new ApiResponse(true, "No users found", null, null)).build();
        }
        LOGGER.info("Returning {} users", ((List<?>) userData.get("users")).size());
        return Response.ok(new ApiResponse(true, "Users retrieved successfully", null, userData)).build();
    }

    /**
     * Exports users as CSV with all filters and sorting (no pagination).
     *
     * @param id              User ID filter.
     * @param email           Email filter.
     * @param name            Name filter.
     * @param phone           Phone filter.
     * @param accountStateStr Account state filter.
     * @param roleStr         Role filter.
     * @param officeStr       Office filter.
     * @param parameterStr    Sort parameter.
     * @param orderStr        Sort order.
     * @param jwtToken        JWT authentication token.
     * @return CSV file with all matching users.
     */
    @GET
    @Path("/export/csv")
    @Produces("text/csv")
    public Response exportUsersToCSV(
            @QueryParam("id") Long id,
            @QueryParam("email") String email,
            @QueryParam("name") String name,
            @QueryParam("phone") String phone,
            @QueryParam("accountState") String accountStateStr,
            @QueryParam("role") String roleStr,
            @QueryParam("office") String officeStr,
            @QueryParam("isManager") Boolean userIsManager,
            @QueryParam("isAdmin") Boolean userIsAdmin,
            @QueryParam("isManaged") Boolean userIsManaged,
            @QueryParam("parameter") @DefaultValue("name") String parameterStr,
            @QueryParam("order") @DefaultValue("ASCENDING") String orderStr,
            @QueryParam("language") @DefaultValue("EN") String languageStr,
            @CookieParam("jwt") String jwtToken) {
        LOGGER.info("CSV export request for users with filters: id={}, email={}, name={}, phone={}", id, email, name, phone);
        boolean isAdmin = JWTUtil.isUserAdmin(jwtToken);
        if (jwtToken == null || jwtToken.isEmpty()) {
            LOGGER.warn("Missing JWT token in user CSV export request");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing token")
                    .build();
        }
        if(accountStateStr != null && !accountStateStr.isEmpty()) {
            if(!JWTUtil.isUserAdmin(jwtToken)){
                LOGGER.warn("Unauthorized access to account state filter without admin privileges");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("Forbidden")
                        .build();
            }
        }
        AccountState accountState = accountStateStr != null ? AccountState.valueOf(SearchUtils.normalizeString(accountStateStr)) : null;
        Office office = officeStr != null ? Office.fromFieldName(SearchUtils.normalizeString(officeStr)) : null;
        Language language = Language.fromFieldName(SearchUtils.normalizeString(languageStr));
        Parameter parameter = Parameter.fromFieldName(SearchUtils.normalizeString(parameterStr));
        OrderBy orderBy = OrderBy.fromFieldName(SearchUtils.normalizeString(orderStr));

        // Call the service to generate CSV (to be implemented)
        byte[] csvData = userService.generateUsersCSV(id, email, name, phone,
                accountState, roleStr, office, userIsManager, userIsAdmin, userIsManaged, language,
                parameter, orderBy, isAdmin);

        return Response.ok(new ByteArrayInputStream(csvData))
                .header("Content-Disposition", "attachment; filename=users.csv")
                .build();
    }

    /**
     * Exports users as XLSX with all filters and sorting (no pagination).
     *
     * @param id              User ID filter.
     * @param email           Email filter.
     * @param name            Name filter.
     * @param phone           Phone filter.
     * @param accountStateStr Account state filter.
     * @param roleStr         Role filter.
     * @param officeStr       Office filter.
     * @param parameterStr    Sort parameter.
     * @param orderStr        Sort order.
     * @param languageStr     Language for header translation.
     * @param jwtToken        JWT authentication token.
     * @return XLSX file with all matching users.
     */
    @GET
    @Path("/export/xlsx")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response exportUsersToXLSX(
            @QueryParam("id") Long id,
            @QueryParam("email") String email,
            @QueryParam("name") String name,
            @QueryParam("phone") String phone,
            @QueryParam("accountState") String accountStateStr,
            @QueryParam("role") String roleStr,
            @QueryParam("office") String officeStr,
            @QueryParam("isManager") Boolean userIsManager,
            @QueryParam("isAdmin") Boolean userIsAdmin,
            @QueryParam("isManaged") Boolean userIsManaged,
            @QueryParam("parameter") @DefaultValue("name") String parameterStr,
            @QueryParam("order") @DefaultValue("ASCENDING") String orderStr,
            @QueryParam("language") @DefaultValue("EN") String languageStr,
            @CookieParam("jwt") String jwtToken) {
        LOGGER.info("XLSX export request for users with filters: id={}, email={}, name={}, phone={}", id, email, name, phone);
        boolean isAdmin = JWTUtil.isUserAdmin(jwtToken);
        if (jwtToken == null || jwtToken.isEmpty()) {
            LOGGER.warn("Missing JWT token in user XLSX export request");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing token")
                    .build();
        }
        if(accountStateStr != null && !accountStateStr.isEmpty()) {
            if(!JWTUtil.isUserAdmin(jwtToken)){
                LOGGER.warn("Unauthorized access to account state filter without admin privileges");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("Forbidden")
                        .build();
            }
        }
        AccountState accountState = accountStateStr != null ? AccountState.valueOf(SearchUtils.normalizeString(accountStateStr)) : null;
        Office office = officeStr != null ? Office.fromFieldName(SearchUtils.normalizeString(officeStr)) : null;
        Language language = Language.fromFieldName(SearchUtils.normalizeString(languageStr));
        Parameter parameter = Parameter.fromFieldName(SearchUtils.normalizeString(parameterStr));
        OrderBy orderBy = OrderBy.fromFieldName(SearchUtils.normalizeString(orderStr));

        byte[] xlsxData = userService.generateUsersXLSX(id, email, name, phone,
                accountState, roleStr, office, userIsManager, userIsAdmin, userIsManaged, language,
                parameter, orderBy, isAdmin);

        return Response.ok(new ByteArrayInputStream(xlsxData))
                .header("Content-Disposition", "attachment; filename=users.xlsx")
                .build();
    }
}