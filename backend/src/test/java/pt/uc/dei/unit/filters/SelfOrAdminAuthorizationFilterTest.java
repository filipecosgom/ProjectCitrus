package pt.uc.dei.unit.filters;

import pt.uc.dei.filters.SelfOrAdminAuthorizationFilter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.utils.ApiResponse;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SelfOrAdminAuthorizationFilterTest {
    @Test
    void testAllowsSelf() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("5");
        UserResponseDTO user = mock(UserResponseDTO.class);
        when(user.getId()).thenReturn(5L);
        when(ctx.getProperty("user")).thenReturn(user);
        when(ctx.getProperty("userIsAdmin")).thenReturn(false);

        SelfOrAdminAuthorizationFilter filter = new SelfOrAdminAuthorizationFilter();
        filter.filter(ctx);
        verify(ctx, never()).abortWith(any());
    }

    @Test
    void testAllowsAdmin() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("5");
        UserResponseDTO user = mock(UserResponseDTO.class);
        when(user.getId()).thenReturn(10L);
        when(ctx.getProperty("user")).thenReturn(user);
        when(ctx.getProperty("userIsAdmin")).thenReturn(true);

        SelfOrAdminAuthorizationFilter filter = new SelfOrAdminAuthorizationFilter();
        filter.filter(ctx);
        verify(ctx, never()).abortWith(any());
    }

    @Test
    void testDeniesIfNotSelfOrAdmin() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("5");
        UserResponseDTO user = mock(UserResponseDTO.class);
        when(user.getId()).thenReturn(10L);
        when(ctx.getProperty("user")).thenReturn(user);
        when(ctx.getProperty("userIsAdmin")).thenReturn(false);

        SelfOrAdminAuthorizationFilter filter = new SelfOrAdminAuthorizationFilter();
        filter.filter(ctx);
        verify(ctx).abortWith(argThat(resp -> {
            Response r = (Response) resp;
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), r.getStatus());
            assertTrue(r.getEntity() instanceof ApiResponse);
            return true;
        }));
    }

    @Test
    void testDoesNothingIfIdOrUserMissing() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn(null);
        when(ctx.getProperty("user")).thenReturn(null);

        SelfOrAdminAuthorizationFilter filter = new SelfOrAdminAuthorizationFilter();
        filter.filter(ctx);
        verify(ctx, never()).abortWith(any());
    }
}
