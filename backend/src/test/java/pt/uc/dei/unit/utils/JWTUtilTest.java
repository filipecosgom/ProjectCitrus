package pt.uc.dei.unit.utils;

import pt.uc.dei.utils.JWTUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.uc.dei.dtos.UserResponseDTO;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {
    @Test
    void testBuildUnauthorizedResponse() {
        var response = JWTUtil.buildUnauthorizedResponse("Unauthorized");
        assertEquals(401, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void testExtractUserIdOrAbort_NullOrEmpty() {
        assertThrows(JWTUtil.JwtValidationException.class, () -> JWTUtil.extractUserIdOrAbort(null));
        assertThrows(JWTUtil.JwtValidationException.class, () -> JWTUtil.extractUserIdOrAbort(""));
    }

    @Test
    void testExtractUserIdOrAbort_InvalidToken() {
        assertThrows(JWTUtil.JwtValidationException.class, () -> JWTUtil.extractUserIdOrAbort("invalid.token"));
    }

    @Test
    void testIsUserAdmin_InvalidToken() {
        assertFalse(JWTUtil.isUserAdmin("invalid.token"));
    }

    // For real JWT generation/validation, you would need a valid secret and DTOs.
    // Here is a static test for validateToken with a mock token and secret.
    @Test
    void testValidateToken_Mock() {
        String key = "0123456789abcdef0123456789abcdef"; // 32 chars = 256 bits
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        JWTUtil.SECRET_KEY = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setSubject("123")
                .claim("userIsAdmin", true)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000))
                .setExpiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        try (MockedStatic<JWTUtil> utilMock = Mockito.mockStatic(JWTUtil.class, Mockito.CALLS_REAL_METHODS)) {
            utilMock.when(() -> JWTUtil.validateToken(token)).thenCallRealMethod();
            // If JWTUtil.validateToken(token) needs the secret, make sure it uses the same one
            Claims claims = JWTUtil.validateToken(token); // Ensure JWTUtil uses the correct secret
            assertEquals("123", claims.getSubject());
            assertTrue(claims.get("userIsAdmin", Boolean.class));
        }
    }
}
