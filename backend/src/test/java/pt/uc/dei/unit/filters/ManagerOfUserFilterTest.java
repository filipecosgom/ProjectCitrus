package pt.uc.dei.unit.filters;

import pt.uc.dei.filters.ManagerOfUserFilter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagerOfUserFilterTest {
    @Test
    void testAllowsManager() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("42");

        UserResponseDTO requester = mock(UserResponseDTO.class);
        when(requester.getId()).thenReturn(99L);
        when(ctx.getProperty("user")).thenReturn(requester);

        UserService userService = mock(UserService.class);
        when(userService.checkIfManagerOfUser(42L, 99L)).thenReturn(true);

        ManagerOfUserFilter filter = new ManagerOfUserFilter();
        setField(filter, "userService", userService);
        filter.filter(ctx);

        verify(ctx, never()).abortWith(any());
    }

    @Test
    void testDeniesIfNotManager() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("42");

        UserResponseDTO requester = mock(UserResponseDTO.class);
        when(requester.getId()).thenReturn(99L);
        when(ctx.getProperty("user")).thenReturn(requester);

        UserService userService = mock(UserService.class);
        when(userService.checkIfManagerOfUser(42L, 99L)).thenReturn(false);

        ManagerOfUserFilter filter = new ManagerOfUserFilter();
        setField(filter, "userService", userService);
        filter.filter(ctx);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(captor.capture());
        Response response = captor.getValue();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse);
    }

    @Test
    void testDeniesIfIdNotNumber() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> pathParams = mock(MultivaluedMap.class);
        when(ctx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPathParameters()).thenReturn(pathParams);
        when(pathParams.getFirst("id")).thenReturn("notANumber");

        UserResponseDTO requester = mock(UserResponseDTO.class);
        when(ctx.getProperty("user")).thenReturn(requester);

        ManagerOfUserFilter filter = new ManagerOfUserFilter();
        filter.filter(ctx);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(captor.capture());
        Response response = captor.getValue();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse);
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

        ManagerOfUserFilter filter = new ManagerOfUserFilter();
        filter.filter(ctx);

        verify(ctx, never()).abortWith(any());
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
