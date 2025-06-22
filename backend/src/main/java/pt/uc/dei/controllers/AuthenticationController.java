package pt.uc.dei.controllers;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import pt.uc.dei.dtos.*;
import pt.uc.dei.services.*;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import jakarta.inject.Inject;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.TwoFactorUtil;

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

    /**
     * Logger for tracking authentication requests
     */
    private final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);

    @Inject
    private AuthenticationService authenticationService;

    /**
     * Injected UserService to handle login operations
     */
    @Inject
    private UserService userService;

    /**
     * Injected TokenService for additional token-related operations
     */
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
     * otherwise HTTP 401 (Unauthorized) if login fails.
     */
    @POST
    @Path("/login") // Defines the login endpoint
    @Consumes(MediaType.APPLICATION_JSON) // Accepts JSON payload
    @Produces(MediaType.APPLICATION_JSON) // Ensures response is JSON
    public Response login(@Valid LoginDTO user) {
        /*if(!TwoFactorUtil.validateCode(user.getAuthenticationCode())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Invalid Auth Code", "errorInvalidAuthCode", null))
                    .build();
        }*/
        /*if(!authenticationService.checkAuthenticationCode(user)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Invalid two factor code", "errorInvalidAuthCode", null))
                    .build();
        }*/
        // Attempt to authenticate user and generate JWT token
        String token = authenticationService.loginUser(user);
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
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletResponse response) {
        // Use response.header to overwrite the previous cookie setting
        response.setHeader("Set-Cookie",
                "jwt=; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT;");
        return Response.ok(new ApiResponse(true, "Logged out successfully", null, null)).build();
    }

    @POST
    @Path("/password-reset")
    @Consumes(MediaType.APPLICATION_JSON) // Accepts JSON payload
    @Produces(MediaType.APPLICATION_JSON) // Ensures response is JSON
    public Response requestPasswordReset(JsonObject emailJSON, @HeaderParam("Accept-Language") String language) {
        if(language.trim() == "" || language.trim() == null) {
            LOGGER.error("Language is empty");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid request: missing language parameter", "errorMissingLanguage", null))
                    .build();
        }
        String email = emailJSON.getString("email");

        if (email == null || email.isEmpty()) {
            LOGGER.error("Email is null or empty");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid request: missing email", "errorMissingEmail", null))
                    .build();
        }
        if(!userService.findIfUserExists(email)) {
            LOGGER.error("User not found");
            return Response.status(Response.Status.CREATED)
                    .entity(new ApiResponse(true, "Password reset token generated successfully", null, null))
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
            emailService.sendPasswordResetEmail(email, token, language);

            return Response.status(Response.Status.CREATED)
                    .entity(new ApiResponse(true, "Password reset token generated successfully", null, null))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Password reset error for {}: {}", email, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Password reset failed", "errorServerIssue", null))
                    .build();
        }
    }

    @GET
    @Path("/password-reset")
    @Consumes(MediaType.APPLICATION_JSON) // Accepts JSON payload
    @Produces(MediaType.APPLICATION_JSON) // Ensures response is JSON
    public Response checkPasswordReset(@HeaderParam("token") String passwordResetTokenValue) {
        if (passwordResetTokenValue == null || passwordResetTokenValue.isEmpty()) {
            LOGGER.error("Password update failed due to missing token");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid request: missing password reset token", "errorMissingPasswordResetToken", null))
                    .build();
        }
        PasswordResetTokenDTO passwordResetTokenDTO = new PasswordResetTokenDTO(passwordResetTokenValue);
        PasswordResetTokenDTO passwordResetToken = tokenService.getPasswordResetTokenByValue(passwordResetTokenDTO);

        if(tokenService.isTokenExpired(passwordResetToken)){
            LOGGER.error("Password reset attempt with token {} expired", passwordResetTokenDTO.getTokenValue());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Invalid request: expired password reset token", "errorExpiredPasswordResetToken", null))
                    .build();
        }
        else {
            LOGGER.info("Password reset token {} valid", passwordResetTokenDTO.getTokenValue());
            return Response.status(Response.Status.OK)
                    .entity(new ApiResponse(true, "Valid password reset token", "successPasswordResetToken", null))
                    .build();
        }
    }


    @PATCH
    @Path("/password-reset")
    @Consumes(MediaType.APPLICATION_JSON) // Accepts JSON payload
    @Produces(MediaType.APPLICATION_JSON) // Ensures response is JSON
    public Response updatePassword(@HeaderParam("token") String passwordResetTokenValue, JsonObject newPasswordJSON) {
        String newPassword = newPasswordJSON.getString("password");
        if (newPassword == null || newPassword.isEmpty()) {
            LOGGER.error("Password update failed due to missing password");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid request: missing password", "errorMissingPassword", null))
                    .build();
        }
        if (passwordResetTokenValue == null || passwordResetTokenValue.isEmpty()) {
            LOGGER.error("Password update failed due to missing token");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid request: missing password reset token", "errorMissingPasswordResetToken", null))
                    .build();
        }
        PasswordResetTokenDTO passwordResetTokenDTO = new PasswordResetTokenDTO(passwordResetTokenValue);
        PasswordResetTokenDTO passwordResetToken = tokenService.getPasswordResetTokenByValue(passwordResetTokenDTO);

        if(tokenService.isTokenExpired(passwordResetToken)){
            LOGGER.error("Password reset attempt with token {} expired", passwordResetTokenDTO.getTokenValue());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Invalid request: expired password reset token", "errorExpiredPasswordResetToken", null))
                    .build();
        }
        if (authenticationService.setNewPassword(passwordResetToken, newPassword)) {
            LOGGER.info("Password reset successfully");
            return Response.status(Response.Status.OK)
                    .entity(new ApiResponse(true, "Password reset sucessfully", "successPasswordResetSuccess", null))
                    .build();
        }
        else {
            LOGGER.error("Password reset failed");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Password reset failed", "errorPasswordResetFailed", null))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON) // Accepts JSON payload
    @Produces(MediaType.APPLICATION_JSON) // Ensures response is JSON
    public Response requestAuthCode(@Valid RequestAuthCodeDTO requester) {
        try {
            String authCode = authenticationService.getAuthCode(requester);
            if (authCode == null) {
                LOGGER.error("Invalid code request for {}", requester.getEmail());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Unauthorized request", "errorInvalidCodeRequest", null))
                        .build();
            }
            return Response.status(Response.Status.OK)
                    .entity(new ApiResponse(true, "Authentication code requested sucessfully", null, Map.of("authCode", authCode)))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Authentication request failed for {}: {}", requester.getEmail(), e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Authentication request failed", "errorServerIssue", null))
                    .build();
        }
    }
}