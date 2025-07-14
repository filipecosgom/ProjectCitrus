package pt.uc.dei.unit.filters;

import pt.uc.dei.filters.AuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.container.ResourceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.uc.dei.dtos.ConfigurationDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.services.AuthenticationService;
import pt.uc.dei.services.ConfigurationService;
import pt.uc.dei.utils.JWTUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthenticationFilterTest {
    private AuthenticationFilter filter;
    private AuthenticationService authenticationService;
    private ConfigurationService configurationService;
    private JWTUtil jwtUtil;
    private ResourceInfo resourceInfo;
    private ContainerRequestContext ctx;

    @BeforeEach
    void setUp() {
        authenticationService = mock(AuthenticationService.class);
        configurationService = mock(ConfigurationService.class);
        jwtUtil = mock(JWTUtil.class);
        resourceInfo = mock(ResourceInfo.class);
        ctx = mock(ContainerRequestContext.class);
        filter = new AuthenticationFilter();
        // Inject mocks via reflection (since @Inject is not used in test)
        setField(filter, "authenticationService", authenticationService);
        setField(filter, "configurationService", configurationService);
        setField(filter, "jwtUtil", jwtUtil);
        setField(filter, "resourceInfo", resourceInfo);
    }

    @Test
    void testAbortIfMissingToken() {
        when(resourceInfo.getResourceMethod()).thenReturn(AuthenticationFilterTest.class.getMethods()[0]);
        when(resourceInfo.getResourceClass()).thenReturn((Class) Object.class);
        when(ctx.getCookies()).thenReturn(new HashMap<>());
        filter.filter(ctx);
        verify(ctx).abortWith(
                argThat(response -> ((Response) response).getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
    }

    @Test
    void testAbortIfEmptyToken() {
        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("jwt", new Cookie("jwt", ""));
        when(resourceInfo.getResourceMethod()).thenReturn(AuthenticationFilterTest.class.getMethods()[0]);
        when(resourceInfo.getResourceClass()).thenReturn((Class) Object.class);
        when(ctx.getCookies()).thenReturn(cookies);
        filter.filter(ctx);
        verify(ctx).abortWith(
                argThat(response -> ((Response) response).getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
    }

    @Test
    void testAbortIfInvalidToken() {
        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("jwt", new Cookie("jwt", "invalid"));
        when(resourceInfo.getResourceMethod()).thenReturn(AuthenticationFilterTest.class.getMethods()[0]);
        when(resourceInfo.getResourceClass()).thenReturn((Class) Object.class);
        when(ctx.getCookies()).thenReturn(cookies);
        try (MockedStatic<JWTUtil> jwtUtilMock = Mockito.mockStatic(JWTUtil.class)) {
            jwtUtilMock.when(() -> JWTUtil.validateToken("invalid")).thenThrow(new JwtException("Invalid"));
            filter.filter(ctx);
            verify(ctx).abortWith(argThat(
                    response -> ((Response) response).getStatus() == Response.Status.FORBIDDEN.getStatusCode()));
        }
    }

    @Test
    void testAbortIfTokenExpired() {
        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("jwt", new Cookie("jwt", "valid"));
        when(resourceInfo.getResourceMethod()).thenReturn(AuthenticationFilterTest.class.getMethods()[0]);
        when(resourceInfo.getResourceClass()).thenReturn((Class) Object.class);
        when(ctx.getCookies()).thenReturn(cookies);
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 10000));
        when(claims.getSubject()).thenReturn("1");
        try (MockedStatic<JWTUtil> jwtUtilMock = Mockito.mockStatic(JWTUtil.class)) {
            jwtUtilMock.when(() -> JWTUtil.validateToken("valid")).thenReturn(claims);
            filter.filter(ctx);
            verify(ctx).abortWith(argThat(
                    response -> ((Response) response).getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
        }
    }

    @Test
    void testAbortIfUserNotFound() {
        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("jwt", new Cookie("jwt", "valid"));
        when(resourceInfo.getResourceMethod()).thenReturn(AuthenticationFilterTest.class.getMethods()[0]);
        when(resourceInfo.getResourceClass()).thenReturn((Class) Object.class);
        when(ctx.getCookies()).thenReturn(cookies);
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 10000));
        when(claims.getSubject()).thenReturn("1");
        try (MockedStatic<JWTUtil> jwtUtilMock = Mockito.mockStatic(JWTUtil.class)) {
            jwtUtilMock.when(() -> JWTUtil.validateToken("valid")).thenReturn(claims);
            when(authenticationService.getSelfInformation(1L)).thenReturn(null);
            filter.filter(ctx);
            verify(ctx).abortWith(argThat(
                    response -> ((Response) response).getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()));
        }
    }

    @Test
    void testSetsUserPropertiesAndRefreshesToken() {
        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put("jwt", new Cookie("jwt", "valid"));
        when(resourceInfo.getResourceMethod()).thenReturn(AuthenticationFilterTest.class.getMethods()[0]);
        when(resourceInfo.getResourceClass()).thenReturn((Class) Object.class);
        when(ctx.getCookies()).thenReturn(cookies);
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 10000));
        when(claims.getSubject()).thenReturn("1");
        try (MockedStatic<JWTUtil> jwtUtilMock = Mockito.mockStatic(JWTUtil.class)) {
            jwtUtilMock.when(() -> JWTUtil.validateToken("valid")).thenReturn(claims);
            UserResponseDTO user = mock(UserResponseDTO.class);
            when(user.getUserIsAdmin()).thenReturn(true);
            when(user.getUserIsManager()).thenReturn(false);
            when(authenticationService.getSelfInformation(1L)).thenReturn(user);
            // Simulate token about to expire
            when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 1000));
            ConfigurationDTO config = mock(ConfigurationDTO.class);
            when(config.getLoginTime()).thenReturn(60);
            when(configurationService.getLatestConfiguration()).thenReturn(config);
            when(jwtUtil.generateToken(user)).thenReturn("newToken");
            jwtUtilMock.when(() -> JWTUtil.getExpiration("newToken"))
                    .thenReturn(new Date(System.currentTimeMillis() + 3600000));
            filter.filter(ctx);
            verify(ctx).setProperty(eq("user"), eq(user));
            verify(ctx).setProperty(eq("userIsAdmin"), eq(true));
            verify(ctx).setProperty(eq("userIsManager"), eq(false));
            verify(ctx).setProperty(eq("newCookie"), any(NewCookie.class));
            verify(ctx).setProperty(eq("newExpiration"), any(Date.class));
        }
    }

    // Helper for reflection field injection
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
