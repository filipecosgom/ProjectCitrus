package pt.uc.dei.controllers;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.resource.spi.AdministeredObject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.annotations.AdminOnly;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.services.CycleService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing cycle-related operations.
 * <p>
 * Provides endpoints for CRUD operations on cycles, including
 * creation, retrieval, updating, and filtering functionality.
 */
@AdminOnly
@Path("/cycles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CycleController {

    /**
     * Logger instance for logging operations within this controller.
     */
    private static final Logger LOGGER = LogManager.getLogger(CycleController.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @EJB
    private CycleService cycleService;

    /**
     * Creates a new cycle.
     *
     * @param cycleDTO The cycle creation data
     * @return Response with the created cycle DTO
     */
    @POST
    public Response createCycle(@Valid CycleDTO cycleDTO, @CookieParam("jwt") String jwtToken) {
        Long userId = JWTUtil.getUserIdFromToken(jwtToken);
                cycleDTO.setAdminId(userId);
                try {
            LOGGER.info("Creating new cycle from {} to {} with admin {}", 
                       cycleDTO.getStartDate(), 
                       cycleDTO.getEndDate(),
                       cycleDTO.getAdminId());

            CycleDTO createdCycle = cycleService.createCycle(cycleDTO);
            return Response.status(Response.Status.CREATED)
                    .entity(new ApiResponse(true, "Cycle created successfully", "success", createdCycle))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid request for cycle creation: {}", e.getMessage());
            String errorCode = getValidationErrorCode(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                    .build();

        } catch (IllegalStateException e) {
            LOGGER.warn("Business rule violation for cycle creation: {}", e.getMessage());
            String errorCode = getConflictErrorCode(e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                    .build();

        } catch (EJBException e) {
            Throwable cause = e.getCause();
            
            if (cause instanceof IllegalStateException) {
                String errorCode = getConflictErrorCode(cause.getMessage());
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ApiResponse(false, cause.getMessage(), errorCode, null))
                        .build();
                        
            } else if (cause instanceof IllegalArgumentException) {
                String errorCode = getValidationErrorCode(cause.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse(false, cause.getMessage(), errorCode, null))
                        .build();
                        
            } else {
                LOGGER.error("EJB error in operation", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                        .build();
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error creating cycle", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Updates an existing cycle.
     *
     * @param cycleUpdateDTO The cycle update data
     * @return Response with the updated cycle DTO
     */
    @PATCH
    public Response updateCycle(@Valid CycleUpdateDTO cycleUpdateDTO) {
        try {
            LOGGER.info("Updating cycle with ID: {}", cycleUpdateDTO.getId());

            CycleDTO updatedCycle = cycleService.updateCycle(cycleUpdateDTO);
            return Response.ok(new ApiResponse(true, "Cycle updated successfully", "success", updatedCycle))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid request for cycle update: {}", e.getMessage());
            String errorCode = getValidationErrorCode(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Business rule violation for cycle update: {}", e.getMessage());
            String errorCode = getConflictErrorCode(e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error updating cycle", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves a cycle by its ID.
     *
     * @param id The cycle ID
     * @return Response with the cycle DTO
     */
    @GET
    @Path("/{id}")
    public Response getCycleById(@PathParam("id") Long id) {
        try {
            LOGGER.debug("Retrieving cycle with ID: {}", id);

            CycleDTO cycle = cycleService.getCycleById(id);
            return Response.ok(new ApiResponse(true, "Cycle retrieved successfully", "success", cycle))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Cycle not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorCycleNotFound", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving cycle with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves all cycles or cycles with filtering options.
     *
     * @param state Optional filter by cycle state
     * @param adminId Optional filter by administrator ID
     * @param startDateFrom Optional filter for cycles starting after this date (ISO format)
     * @param startDateTo Optional filter for cycles starting before this date (ISO format)
     * @param limit Maximum number of results
     * @param offset Starting position for pagination
     * @return Response with list of filtered cycle DTOs
     */
    @GET
    public Response getCyclesWithFilters(@QueryParam("state") CycleState state,
                                        @QueryParam("adminId") Long adminId,
                                        @QueryParam("startDateFrom") String startDateFrom,
                                        @QueryParam("startDateTo") String startDateTo,
                                        @QueryParam("limit") @DefaultValue("50") Integer limit,
                                        @QueryParam("offset") @DefaultValue("0") Integer offset) {
        try {
            LOGGER.debug("Retrieving cycles with filters");

            LocalDate startDateFromParsed = null;
            LocalDate startDateToParsed = null;

            // Parse date strings if provided
            if (startDateFrom != null && !startDateFrom.isEmpty()) {
                try {
                    startDateFromParsed = LocalDate.parse(startDateFrom, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                                  .entity(new ApiResponse(false, "Invalid startDateFrom format. Use ISO format (yyyy-MM-dd)", "errorInvalidDateFormat", null)).build();
                }
            }

            if (startDateTo != null && !startDateTo.isEmpty()) {
                try {
                    startDateToParsed = LocalDate.parse(startDateTo, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                                  .entity(new ApiResponse(false, "Invalid startDateTo format. Use ISO format (yyyy-MM-dd)", "errorInvalidDateFormat", null)).build();
                }
            }

            List<CycleDTO> cycles;
            Map<String, Object> cycleData = new HashMap<>();
            
            // If no filters are provided, return all cycles
            if (state == null && adminId == null && startDateFromParsed == null && startDateToParsed == null) {
                cycleData = cycleService.getAllCycles(offset, limit);
            } else {
                cycleData = cycleService.getCyclesWithFilters(
                    state, adminId, startDateFromParsed, startDateToParsed, limit, offset
                );
            }
            
            return Response.ok(new ApiResponse(true, "Cycles retrieved successfully", "success", cycleData)).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving cycles with filters", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null)).build();
        }
    }

    /**
     * Retrieves the current active cycle.
     *
     * @return Response with the current active cycle DTO or 404 if none exists
     */
    @GET
    @Path("/current")
    public Response getCurrentActiveCycle() {
        try {
            LOGGER.debug("Retrieving current active cycle");

            CycleDTO currentCycle = cycleService.getCurrentActiveCycle();
            if (currentCycle == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "No active cycle found", "errorNoActiveCycle", null))
                        .build();
            }
            
            return Response.ok(new ApiResponse(true, "Current cycle retrieved successfully", "success", currentCycle))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving current active cycle", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves upcoming cycles.
     *
     * @return Response with list of upcoming cycle DTOs
     */
    @GET
    @Path("/upcoming")
    public Response getUpcomingCycles() {
        try {
            LOGGER.debug("Retrieving upcoming cycles");

            List<CycleDTO> upcomingCycles = cycleService.getUpcomingCycles();
            return Response.ok(new ApiResponse(true, "Upcoming cycles retrieved successfully", "success", upcomingCycles)).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving upcoming cycles", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null)).build();
        }
    }

    /**
     * Retrieves cycles managed by a specific administrator.
     *
     * @param adminId The administrator ID
     * @return Response with list of cycle DTOs
     */
    @GET
    @Path("/admin/{adminId}")
    public Response getCyclesByAdmin(@PathParam("adminId") Long adminId) {
        try {
            LOGGER.debug("Retrieving cycles for admin ID: {}", adminId);

            List<CycleDTO> cycles = cycleService.getCyclesByAdmin(adminId);
            return Response.ok(new ApiResponse(true, "Admin cycles retrieved successfully", "success", cycles)).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving cycles for admin ID: {}", adminId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null)).build();
        }
    }

    /**
     * Closes a cycle.
     *
     * @param id The cycle ID
     * @return Response with the updated cycle DTO
     */
    @POST
    @Path("/{id}/close")
    public Response closeCycle(@PathParam("id") Long id) {
        try {
            LOGGER.info("Closing cycle with ID: {}", id);

            CycleDTO closedCycle = cycleService.closeCycle(id);
            return Response.ok(new ApiResponse(true, "Cycle closed successfully", "success", closedCycle))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Cycle not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorCycleNotFound", null))
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot close cycle with ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), "errorCycleAlreadyClosed", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error closing cycle with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Reopens a cycle.
     *
     * @param id The cycle ID
     * @return Response with the updated cycle DTO
     */
    @POST
    @Path("/{id}/reopen")
    public Response reopenCycle(@PathParam("id") Long id) {
        try {
            LOGGER.info("Reopening cycle with ID: {}", id);

            CycleDTO reopenedCycle = cycleService.reopenCycle(id);
            return Response.ok(new ApiResponse(true, "Cycle reopened successfully", "success", reopenedCycle)).build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Cycle not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                          .entity(new ApiResponse(false, e.getMessage(), "errorCycleNotFound", null)).build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot reopen cycle with ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                          .entity(new ApiResponse(false, e.getMessage(), "errorCycleAlreadyOpen", null)).build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error reopening cycle with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null)).build();
        }
    }

    /**
     * Deletes a cycle.
     *
     * @param id The cycle ID
     * @return Response indicating success or failure
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCycle(@PathParam("id") Long id) {
        try {
            LOGGER.info("Deleting cycle with ID: {}", id);

            cycleService.deleteCycle(id);
            return Response.ok(new ApiResponse(true, "Cycle deleted successfully", "success", null)).build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Cycle not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                          .entity(new ApiResponse(false, e.getMessage(), "errorCycleNotFound", null)).build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot delete cycle with ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                          .entity(new ApiResponse(false, e.getMessage(), "errorCycleAlreadyClosed", null))
                          .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error deleting cycle with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null)).build();
        }
    }

    /**
     * Automatically closes expired cycles.
     * This endpoint can be called by a scheduled job or admin to automatically close
     * cycles that have passed their end date.
     *
     * @return Response with the number of cycles that were closed
     */
    @POST
    @Path("/close-expired")
    public Response closeExpiredCycles() {
        try {
            LOGGER.info("Closing expired cycles");

            int closedCount = cycleService.closeExpiredCycles();
            return Response.ok(new ApiResponse(true, "Expired cycles closed successfully", "success", closedCount)).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error closing expired cycles", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null)).build();
        }
    }

    /**
     * Validates if a cycle can be closed.
     *
     * @param id The cycle ID
     * @return Response with validation result and details
     */
    @GET
    @Path("/{id}/can-close")
    public Response canCloseCycle(@PathParam("id") Long id) {
        try {
            LOGGER.debug("Validating if cycle {} can be closed", id);

            Map<String, Object> validation = cycleService.canCloseCycle(id);
            return Response.ok(new ApiResponse(true, "Cycle validation completed", "success", validation))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Cycle not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorCycleNotFound", null))
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot validate cycle with ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), "errorCycleAlreadyClosed", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error validating cycle with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    // Métodos helper para determinar códigos de erro
    private String getValidationErrorCode(String message) {
        if (message.contains("Start date") && message.contains("past")) {
            return "errorStartDatePast";
        } else if (message.contains("Start date") && message.contains("before end date")) {
            return "errorDateValidation";
        } else if (message.contains("Admin user not found")) {
            return "errorMissingAdmin";
        } else if (message.contains("format")) {
            return "errorInvalidDateFormat";
        }
        return "errorInvalidData";
    }

    private String getConflictErrorCode(String message) {
        // Caso específico para users sem manager
        if (message.contains("without manager")) {
            return "errorUsersWithoutManager";
        } else if (message.contains("overlapping") || message.contains("already exists")) {
            return "errorCycleOverlap";
        } else if (message.contains("already closed")) {
            return "errorCycleAlreadyClosed";
        } else if (message.contains("already open")) {
            return "errorCycleAlreadyOpen";
        } else if (message.contains("already started")) {
            return "errorCycleAlreadyStarted";
        } else if (message.contains("would overlap")) {
            return "errorReopenOverlap";
        }
        return "errorConflict";
    }
}
