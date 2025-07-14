package pt.uc.dei.unit.filters;

import pt.uc.dei.filters.AnotherAuthorizationFilter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.utils.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnotherAuthorizationFilterTest {
    @Test
    void testDeniesSelfAccess() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("42");

        UserResponseDTO user = mock(UserResponseDTO.class);
        when(user.getId()).thenReturn(42L);
        when(ctx.getProperty("user")).thenReturn(user);

        AnotherAuthorizationFilter filter = new AnotherAuthorizationFilter();
        filter.filter(ctx);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(captor.capture());
        Response response = captor.getValue();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse);
    }

    @Test
    void testAllowsOtherUserAccess() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("42");

        UserResponseDTO user = mock(UserResponseDTO.class);
        when(user.getId()).thenReturn(99L);
        when(ctx.getProperty("user")).thenReturn(user);

        AnotherAuthorizationFilter filter = new AnotherAuthorizationFilter();
        filter.filter(ctx);

        verify(ctx, never()).abortWith(any());
    }

    @Test
    void testNoIdOrUserDoesNothing() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn(null);
        when(ctx.getProperty("user")).thenReturn(null);

        AnotherAuthorizationFilter filter = new AnotherAuthorizationFilter();
        filter.filter(ctx);

        verify(ctx, never()).abortWith(any());
    }
}
