package pt.uc.dei.utils;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RefreshCookieResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        NewCookie newCookie = (NewCookie) requestContext.getProperty("newCookie");

        if (newCookie != null) {
            responseContext.getHeaders().add("Set-Cookie", newCookie.toString()); // ✅ Sends the refreshed JWT cookie
        }
    }
}