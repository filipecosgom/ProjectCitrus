package pt.uc.dei.controllers;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.uc.dei.annotations.AdminOnly;
import pt.uc.dei.services.SettingsService;
import pt.uc.dei.utils.ApiResponse;

@Path("/settings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SettingsController {

    @EJB
    private SettingsService settingsService;

    @AdminOnly
    @PUT
    @Path("/twofactor")
    public Response updateTwoFactorAuth(@QueryParam("enabled") boolean enabled) {
        boolean success = settingsService.setTwoFactorAuthEnabled(enabled);
        if (success) {
            return Response.ok(new ApiResponse(true, "2FA updated", "success", null)).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiResponse(false, "Failed to update 2FA", "error", null)).build();
        }
    }

    @GET
    @Path("/twofactor")
    public Response getTwoFactorAuthStatus() {
        Boolean enabled = settingsService.getTwoFactorAuthEnabled();
        if (enabled != null) {
            return Response.ok("{\"enabled\":" + enabled + "}").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"Configuração não encontrada\"}").build();
        }
    }
}