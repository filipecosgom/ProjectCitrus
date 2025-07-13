package pt.uc.dei.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@Path("/stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatsController {

    @Inject
    UserService userService;

    @GET
    @Path("/users")
    public Response getUserStats() {
        // Total de utilizadores
        long total = (long) userService.getUsers(null, null, null, null, null, null, null, null, null, null, null, null, 0, 1).get("totalUsers");
        // Perfis completos
        long complete = (long) userService.getUsers(null, null, null, null, pt.uc.dei.enums.AccountState.COMPLETE, null, null, null, null, null, null, null, 0, 1).get("totalUsers");
        // Perfis incompletos
        long incomplete = (long) userService.getUsers(null, null, null, null, pt.uc.dei.enums.AccountState.INCOMPLETE, null, null, null, null, null, null, null, 0, 1).get("totalUsers");
        // Admins
        long admins = (long) userService.getUsers(null, null, null, null, null, null, null, null, true, null, null, null, 0, 1).get("totalUsers");
        // Managers
        long managers = (long) userService.getUsers(null, null, null, null, null, null, null, true, null, null, null, null, 0, 1).get("totalUsers");
        // Users normais
        long users = total - admins - managers;

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("profileCompletion", Map.of("complete", complete, "incomplete", incomplete));
        stats.put("roles", Map.of("admin", admins, "manager", managers, "user", users));

        return Response.ok(new ApiResponse(true, "User stats", null, stats)).build();
    }

    // Aqui podes adicionar endpoints para appraisals, courses, cycles, etc.
}