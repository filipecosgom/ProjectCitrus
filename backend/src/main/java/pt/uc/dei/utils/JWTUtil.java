package pt.uc.dei.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.entities.ConfigurationEntity;
import pt.uc.dei.repositories.ConfigurationRepository;
import java.util.Base64;
import java.util.Date;
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
                .setSubject(user.getEmail()) // Set user's email as subject
                .claim("isAdmin", user.getAdmin()) // Store isAdmin flag
                .claim("isManager", user.getManager()) // Store isManager flag
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

}
