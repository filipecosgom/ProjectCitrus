package pt.uc.dei.unit.filters;

import pt.uc.dei.filters.AdminAuthorizationFilter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pt.uc.dei.utils.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminAuthorizationFilterTest {
    @Test
    void testFilter_AllowsAdmin() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getProperty("userIsAdmin")).thenReturn(true);

        AdminAuthorizationFilter filter = new AdminAuthorizationFilter();
        filter.filter(ctx);

        verify(ctx, never()).abortWith(any());
    }

    @Test
    void testFilter_DeniesNonAdmin() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getProperty("userIsAdmin")).thenReturn(false);

        AdminAuthorizationFilter filter = new AdminAuthorizationFilter();
        filter.filter(ctx);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(captor.capture());
        Response response = captor.getValue();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse);
    }

    @Test
    void testFilter_DeniesWhenAdminPropertyMissing() {
        ContainerRequestContext ctx = mock(ContainerRequestContext.class);
        when(ctx.getProperty("userIsAdmin")).thenReturn(null);

        AdminAuthorizationFilter filter = new AdminAuthorizationFilter();
        filter.filter(ctx);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(ctx).abortWith(captor.capture());
        Response response = captor.getValue();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity() instanceof ApiResponse);
    }
}
