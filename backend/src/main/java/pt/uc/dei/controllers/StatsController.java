package pt.uc.dei.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.uc.dei.services.UserService;
import pt.uc.dei.services.AppraisalService;
import pt.uc.dei.services.CourseService;
import pt.uc.dei.services.CycleService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.CycleState;

import java.util.HashMap;
import java.util.Map;

@Path("/stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatsController {

    @Inject
    UserService userService;
    @Inject
    AppraisalService appraisalService;
    @Inject
    CourseService courseService;
    @Inject
    CycleService cycleService;

    @GET
    @Path("/users")
    public Response getUserStats() {
        // Total de utilizadores
        long total = ((Number) userService.getUsers(null, null, null, null, null, null, null, null, null, null, null, null, 0, 1).get("totalUsers")).longValue();
        // Perfis completos
        long complete = ((Number) userService.getUsers(null, null, null, null, pt.uc.dei.enums.AccountState.COMPLETE, null, null, null, null, null, null, null, 0, 1).get("totalUsers")).longValue();
        // Perfis incompletos
        long incomplete = ((Number) userService.getUsers(null, null, null, null, pt.uc.dei.enums.AccountState.INCOMPLETE, null, null, null, null, null, null, null, 0, 1).get("totalUsers")).longValue();
        // Admins
        long admins = ((Number) userService.getUsers(null, null, null, null, null, null, null, null, true, null, null, null, 0, 1).get("totalUsers")).longValue();
        // Managers
        long managers = ((Number) userService.getUsers(null, null, null, null, null, null, null, true, null, null, null, null, 0, 1).get("totalUsers")).longValue();
        // Users normais
        long users = total - admins - managers;

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("profileCompletion", Map.of("complete", complete, "incomplete", incomplete));
        stats.put("roles", Map.of("admin", admins, "manager", managers, "user", users));

        return Response.ok(new ApiResponse(true, "User stats", null, stats)).build();
    }

    @GET
    @Path("/appraisals")
    public Response getAppraisalStats() {
        long total = appraisalService.countAllAppraisals();
        long inProgress = appraisalService.countAppraisalsByState(AppraisalState.IN_PROGRESS);
        long completed = appraisalService.countAppraisalsByState(AppraisalState.COMPLETED);
        long closed = appraisalService.countAppraisalsByState(AppraisalState.CLOSED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("inProgress", inProgress);
        stats.put("completed", completed);
        stats.put("closed", closed);

        return Response.ok(new ApiResponse(true, "Appraisal stats", null, stats)).build();
    }

    @GET
    @Path("/courses")
    public Response getCourseStats() {
        long total = courseService.countAllCourses();
        long active = courseService.countCoursesByActive(true);
        long inactive = courseService.countCoursesByActive(false);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("inactive", inactive);

        return Response.ok(new ApiResponse(true, "Course stats", null, stats)).build();
    }

    @GET
    @Path("/cycles")
    public Response getCycleStats() {
        long total = cycleService.countAllCycles();
        long open = cycleService.countCyclesByState(CycleState.OPEN);
        long closed = cycleService.countCyclesByState(CycleState.CLOSED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("open", open);
        stats.put("closed", closed);

        return Response.ok(new ApiResponse(true, "Cycle stats", null, stats)).build();
    }
}