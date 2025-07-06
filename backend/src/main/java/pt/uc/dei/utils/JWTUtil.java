package pt.uc.dei.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.ConfigurationEntity;
import pt.uc.dei.repositories.ConfigurationRepository;
import java.util.Base64;
import java.util.Date;
import java.util.List;
/**
 * Utility class for handling JWT authentication.
 * <p>
 * Generates and validates JWT tokens for user authentication.
 * Uses a secret key stored in environment variables for signing the tokens.
 * </p>
 */
@ApplicationScoped // Ensures this bean is managed by CDI and can be injected
public class JWTUtil {

    /**
     * Secret key used for signing JWTs.
     * Retrieved from environment variables for security purposes.
     */
    private static final String SECRET_KEY = System.getenv("SECRET_KEY");

    /**
     * Injecting EJB ConfigurationRepository to fetch configuration settings.
     * Retrieves authentication expiration time.
     */
    @EJB
    private ConfigurationRepository configurationRepository;

    /**
     * Generates a JWT authentication token for the given user.
     *
     * @param user The user DTO containing authentication details.
     * @return A signed JWT token containing user authentication claims.
     */
    public String generateToken(UserResponseDTO user) {
        // Get latest configuration from database
        ConfigurationEntity latestConfiguration = configurationRepository.getLatestConfiguration();
        Integer expirationTime = latestConfiguration.getLoginTime() * 60 * 1000; // Convert minutes to milliseconds
        System.out.println(SECRET_KEY);
        // Create JWT token with user details (email, isAdmin, isManager)
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId())) // Set user's email as subject
                .claim("userIsAdmin", user.getUserIsAdmin()) // Store isAdmin flag
                .claim("userIsManager", user.getUserIsManager()) // Store isManager flag
                .claim("accountState", user.getAccountState())
                .setIssuedAt(new Date()) // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Expiry time
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY)), SignatureAlgorithm.HS256) // Secure signing
                .compact(); // Final compact JWT
    }

    /**
     * Validates a given JWT token and extracts claims.
     *
     * @param token The JWT token to validate.
     * @return Extracted claims from the JWT.
     */
    public static Claims validateToken(String token) {
        // Decode secret key and verify JWT signature
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY))) // Ensure token integrity
                .build()
                .parseClaimsJws(token) // Parse JWT
                .getBody(); // Extract claims
    }

    public static Date getExpiration(String token) {
        return validateToken(token).getExpiration();
    }

    public static Long getIdFromContainerRequestContext(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get("jwt");
        if (cookie == null) {
            return null;
        }
        Claims claims = validateToken(cookie.getValue());
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extracts the user ID from a JWT token in a WebSocket HandshakeRequest.
     *
     * @param request The HandshakeRequest containing cookies.
     * @return The user ID as Long, or null if invalid or missing.
     */
    public static Long getUserIdFromToken(HandshakeRequest request) {
        if (request != null && request.getHeaders().containsKey("cookie")) {
            List<String> cookies = request.getHeaders().get("cookie");
            String jwt = null;
            for (String cookieHeader : cookies) {
                for (String cookie : cookieHeader.split(";")) {
                    String[] parts = cookie.trim().split("=", 2);
                    if (parts.length == 2 && parts[0].equals("jwt")) {
                        jwt = parts[1];
                    }
                }
            }
            if (jwt != null) {
                try {
                    Claims claims = validateToken(jwt);
                    return Long.parseLong(claims.getSubject());
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    // Optionally, keep the String version for direct JWT usage
    /**
     * Extracts the user ID from a JWT token string.
     *
     * @param token The JWT token.
     * @return The user ID as Long, or null if invalid.
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = validateToken(token);
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isUserAdmin(String jwtToken) {
        try {
            Claims claims = validateToken(jwtToken);
            return claims.get("userIsAdmin", Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }

    public static Long extractUserIdOrAbort(String jwtToken) throws JwtValidationException {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new JwtValidationException("Unauthorized: missing JWT token");
        }

        Long userId = JWTUtil.getUserIdFromToken(jwtToken);
        if (userId == null) {
            throw new JwtValidationException("Unauthorized: invalid JWT token");
        }

        return userId;
    }

    public static Response buildUnauthorizedResponse(String message) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ApiResponse(false, message, "errorUnauthorized", null))
                .build();
    }

    // Custom exception to handle auth errors
    public static class JwtValidationException extends Exception {
        public JwtValidationException(String message) {
            super(message);
        }
    }




}
