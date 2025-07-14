package pt.uc.dei.unit.filters;

import pt.uc.dei.filters.ManagerAuthorizationFilter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pt.uc.dei.utils.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagerAuthorizationFilterTest {
    @Test
    void testAllowsManager() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getProperty("userIsManager")).thenReturn(true);
        when(ctx.getProperty("userIsAdmin")).thenReturn(false);

        ManagerAuthorizationFilter filter = new ManagerAuthorizationFilter();
        filter.filter(ctx);

        verify(ctx, never()).abortWith(any());
    }

    @Test
    void testAllowsAdmin() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getProperty("userIsManager")).thenReturn(false);
        when(ctx.getProperty("userIsAdmin")).thenReturn(true);

        ManagerAuthorizationFilter filter = new ManagerAuthorizationFilter();
        filter.filter(ctx);

        verify(ctx, never()).abortWith(any());
    }

    @Test
    void testDeniesNonManagerNonAdmin() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getProperty("userIsManager")).thenReturn(false);
        when(ctx.getProperty("userIsAdmin")).thenReturn(false);

        ManagerAuthorizationFilter filter = new ManagerAuthorizationFilter();
        filter.filter(ctx);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(captor.capture());
        Response response = captor.getValue();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse);
    }

    @Test
    void testDeniesWhenPropertiesMissing() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getProperty("userIsManager")).thenReturn(null);
        when(ctx.getProperty("userIsAdmin")).thenReturn(null);

        ManagerAuthorizationFilter filter = new ManagerAuthorizationFilter();
        filter.filter(ctx);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(captor.capture());
        Response response = captor.getValue();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse);
    }
}
