package pt.uc.dei.unit.filters;

import pt.uc.dei.filters.RefreshCookieResponseFilter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshCookieResponseFilterTest {
    @Test
    void testAddsCookieIfPresentAndNotLogout() {
        ContainerRequestContext reqCtx = mock(ContainerRequestContext.class);
        ContainerResponseContext resCtx = mock(ContainerResponseContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        when(reqCtx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPath()).thenReturn("/api/someendpoint");
        NewCookie cookie = new NewCookie("auth", "token");
        when(reqCtx.getProperty("newCookie")).thenReturn(cookie);
        jakarta.ws.rs.core.MultivaluedMap<String, Object> headers = mock(jakarta.ws.rs.core.MultivaluedMap.class);
        when(resCtx.getHeaders()).thenReturn(headers);

        RefreshCookieResponseFilter filter = new RefreshCookieResponseFilter();
        filter.filter(reqCtx, resCtx);

        verify(headers).add(eq("Set-Cookie"), eq(cookie.toString()));
    }

    @Test
    void testDoesNotAddCookieIfNotPresent() {
        ContainerRequestContext reqCtx = mock(ContainerRequestContext.class);
        ContainerResponseContext resCtx = mock(ContainerResponseContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        when(reqCtx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPath()).thenReturn("/api/someendpoint");
        when(reqCtx.getProperty("newCookie")).thenReturn(null);
        jakarta.ws.rs.core.MultivaluedMap<String, Object> headers = mock(jakarta.ws.rs.core.MultivaluedMap.class);
        when(resCtx.getHeaders()).thenReturn(headers);

        RefreshCookieResponseFilter filter = new RefreshCookieResponseFilter();
        filter.filter(reqCtx, resCtx);

        verify(headers, never()).add(anyString(), any());
    }

    @Test
    void testSkipsIfLogoutPath() {
        ContainerRequestContext reqCtx = mock(ContainerRequestContext.class);
        ContainerResponseContext resCtx = mock(ContainerResponseContext.class);
        UriInfo uriInfo = mock(UriInfo.class);
        when(reqCtx.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPath()).thenReturn("/api/logout");
        NewCookie cookie = new NewCookie("auth", "token");
        when(reqCtx.getProperty("newCookie")).thenReturn(cookie);
        MultivaluedMap<String, Object> headers = mock(jakarta.ws.rs.core.MultivaluedMap.class);
        when(resCtx.getHeaders()).thenReturn(headers);

        RefreshCookieResponseFilter filter = new RefreshCookieResponseFilter();
        filter.filter(reqCtx, resCtx);

        verify(headers, never()).add(anyString(), any());
    }
}
