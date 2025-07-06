package pt.uc.dei.controllers;

import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.AppraisalResponseDTO;
import pt.uc.dei.dtos.AppraisalStatsDTO;
import pt.uc.dei.dtos.CreateAppraisalDTO;
import pt.uc.dei.dtos.UpdateAppraisalDTO;
import pt.uc.dei.enums.*;
import pt.uc.dei.services.AppraisalService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.annotations.AdminOnly;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.SearchUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * REST Controller for managing appraisal-related operations.
 * <p>
 * Provides endpoints for CRUD operations on appraisals, including
 * creation, retrieval, updating, and filtering functionality.
 */

@Path("/appraisals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppraisalController {

    /**
     * Logger instance for logging operations within this controller.
     */
    private static final Logger LOGGER = LogManager.getLogger(AppraisalController.class);

    @EJB
    private AppraisalService appraisalService;

    /**
     * Updates an existing appraisal.
     *
     * @param updateAppraisalDTO The appraisal update data
     * @return Response with the updated appraisal DTO
     */

    @PATCH
    public Response updateAppraisal(UpdateAppraisalDTO updateAppraisalDTO, @CookieParam("jwt") String jwtToken) {
        Response validationResponse = validateUpdateAppraisalDTO(updateAppraisalDTO);
        if (validationResponse != null)
            return validationResponse;
        try {
            Long managerId = JWTUtil.extractUserIdOrAbort(jwtToken);
            if (appraisalService.checkIfManagerOfUser(updateAppraisalDTO.getId(), managerId)) {
                LOGGER.info("Manager with ID {} is authorized to update appraisal with ID: {}", managerId,
                        updateAppraisalDTO.getId());
            } else {
                LOGGER.warn("Manager with ID {} is not authorized to update appraisal with ID: {}", managerId,
                        updateAppraisalDTO.getId());
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(new ApiResponse(false, "You are not authorized to update this appraisal",
                                "errorUnauthorized", null))
                        .build();
            }
            try {
                LOGGER.info("Updating appraisal with ID: {}", updateAppraisalDTO.getId());
                AppraisalDTO updatedAppraisal = appraisalService.updateAppraisal(updateAppraisalDTO);
                return Response.ok(new ApiResponse(true, "Appraisal updated successfully", "success", updatedAppraisal))
                        .build();

            } catch (IllegalArgumentException e) {
                LOGGER.warn("Invalid request for appraisal update: {}", e.getMessage());
                String errorCode = getValidationErrorCode(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                        .build();
            } catch (IllegalStateException e) {
                LOGGER.warn("Business rule violation for appraisal update: {}", e.getMessage());
                String errorCode = getConflictErrorCode(e.getMessage());
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                        .build();
            } catch (Exception e) {
                LOGGER.error("Unexpected error updating appraisal", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                        .build();
            }
        } catch (JWTUtil.JwtValidationException ex) {
            return JWTUtil.buildUnauthorizedResponse(ex.getMessage());
        }
    }

    /**
     * Retrieves an appraisal by its ID.
     *
     * @param id The appraisal ID
     * @return Response with the appraisal DTO
     */
    @GET
    @Path("/{id}")
    public Response getAppraisalById(@PathParam("id") Long id) {
        try {
            LOGGER.debug("Retrieving appraisal with ID: {}", id);
            AppraisalDTO appraisal = appraisalService.getAppraisalById(id);
            return Response.ok(new ApiResponse(true, "Appraisal retrieved successfully", "success", appraisal))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorAppraisalNotFound", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves appraisals with filtering options.
     *
     * @param appraisedUserId   Optional filter by appraised user ID
     * @param appraisingUserId  Optional filter by appraising user ID (manager)
     * @param cycleId           Optional filter by cycle ID
     * @param appraisalStateStr Optional filter by appraisal state
     * @param limit             Maximum number of results
     * @param offset            Starting position for pagination
     * @return Response with list of filtered appraisal DTOs
     */
    @GET
    public Response getAppraisalsWithFilters(@QueryParam("appraisedUserId") Long appraisedUserId,
            @QueryParam("appraisedUserName") String appraisedUserName,
            @QueryParam("appraisedUserEmail") String appraisedUserEmail,
            @QueryParam("appraisingUserId") Long appraisingUserId,
            @QueryParam("appraisingUserName") String appraisingUserName,
            @QueryParam("appraisingUserEmail") String appraisingUserEmail,
            @QueryParam("cycleId") Long cycleId,
            @QueryParam("state") String appraisalStateStr,
            @QueryParam("parameter") @DefaultValue("creationDate") String parameterStr,
            @QueryParam("order") @DefaultValue("DESCENDING") String orderStr,
            @QueryParam("limit") @DefaultValue("10") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) {
        AppraisalState state = appraisalStateStr != null
                ? AppraisalState.valueOf(SearchUtils.normalizeString(appraisalStateStr))
                : null;
        AppraisalParameter parameter = AppraisalParameter.fromFieldName(SearchUtils.normalizeString(parameterStr));
        OrderBy orderBy = OrderBy.fromFieldName(SearchUtils.normalizeString(orderStr));
        try {
            LOGGER.debug("Retrieving appraisals with filters");

            Map<String, Object> appraisalData = appraisalService.getAppraisalsWithFilters(
                    appraisedUserId, appraisedUserName, appraisedUserEmail, appraisingUserId, appraisingUserName,
                    appraisingUserEmail, cycleId, state, parameter, orderBy, limit, offset);
            return Response.ok(new ApiResponse(true, "Appraisals retrieved successfully", "success", appraisalData))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals with filters", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves all appraisals for a specific user (as appraised).
     *
     * @param userId The user ID
     * @return Response with list of appraisal DTOs
     */
    @GET
    @Path("/user/{userId}")
    public Response getAppraisalsByUser(@PathParam("userId") Long userId) {
        try {
            LOGGER.debug("Retrieving appraisals for user ID: {}", userId);

            List<AppraisalDTO> appraisals = appraisalService.getAppraisalsByAppraisedUser(userId);
            return Response.ok(new ApiResponse(true, "User appraisals retrieved successfully", "success", appraisals))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals for user ID: {}", userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves all appraisals created by a specific manager.
     *
     * @param managerId The manager user ID
     * @return Response with list of appraisal DTOs
     */
    @GET
    @Path("/manager/{managerId}")
    public Response getAppraisalsByManager(@PathParam("managerId") Long managerId) {
        try {
            LOGGER.debug("Retrieving appraisals created by manager ID: {}", managerId);

            List<AppraisalDTO> appraisals = appraisalService.getAppraisalsByManager(managerId);
            return Response
                    .ok(new ApiResponse(true, "Manager appraisals retrieved successfully", "success", appraisals))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals for manager ID: {}", managerId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves all appraisals within a specific cycle.
     *
     * @param cycleId The cycle ID
     * @return Response with list of appraisal DTOs
     */
    @GET
    @Path("/cycle/{cycleId}")
    public Response getAppraisalsByCycle(@PathParam("cycleId") Long cycleId) {
        try {
            LOGGER.debug("Retrieving appraisals for cycle ID: {}", cycleId);

            List<AppraisalDTO> appraisals = appraisalService.getAppraisalsByCycle(cycleId);
            return Response.ok(new ApiResponse(true, "Cycle appraisals retrieved successfully", "success", appraisals))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals for cycle ID: {}", cycleId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Completes an appraisal.
     *
     * @param id The appraisal ID
     * @return Response with the updated appraisal DTO
     */
    @POST
    @Path("/{id}/complete")
    public Response completeAppraisal(@PathParam("id") Long id) {
        try {
            LOGGER.info("Completing appraisal with ID: {}", id);

            AppraisalDTO completedAppraisal = appraisalService.completeAppraisal(id);
            return Response.ok(new ApiResponse(true, "Appraisal completed successfully", "success", completedAppraisal))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorAppraisalNotFound", null))
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot complete appraisal with ID {}: {}", id, e.getMessage());
            String errorCode = getConflictErrorCode(e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error completing appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Closes an appraisal.
     *
     * @param id The appraisal ID
     * @return Response with the updated appraisal DTO
     */
    @POST
    @Path("/{id}/close")
    public Response closeAppraisal(@PathParam("id") Long id) {
        try {
            LOGGER.info("Closing appraisal with ID: {}", id);

            AppraisalDTO closedAppraisal = appraisalService.closeAppraisal(id);
            return Response.ok(new ApiResponse(true, "Appraisal closed successfully", "success", closedAppraisal))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorAppraisalNotFound", null))
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot close appraisal with ID {}: {}", id, e.getMessage());
            String errorCode = getConflictErrorCode(e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error closing appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Deletes an appraisal.
     *
     * @param id The appraisal ID
     * @return Response indicating success or failure
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAppraisal(@PathParam("id") Long id) {
        try {
            LOGGER.info("Deleting appraisal with ID: {}", id);

            appraisalService.deleteAppraisal(id);
            return Response.ok(new ApiResponse(true, "Appraisal deleted successfully", "success", null))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorAppraisalNotFound", null))
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot delete appraisal with ID {}: {}", id, e.getMessage());
            String errorCode = getConflictErrorCode(e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), errorCode, null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error deleting appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Gets appraisal statistics for a user.
     *
     * @param userId The user ID
     * @return Response with appraisal statistics
     */
    @GET
    @Path("/stats/{userId}")
    public Response getAppraisalStats(@PathParam("userId") Long userId) {
        try {
            LOGGER.debug("Getting appraisal statistics for user ID: {}", userId);

            AppraisalStatsDTO stats = appraisalService.getAppraisalStats(userId);
            return Response.ok(new ApiResponse(true, "Appraisal statistics retrieved successfully", "success", stats))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error getting appraisal statistics for user ID: {}", userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Closes specific appraisals by their IDs.
     * Only COMPLETED appraisals in OPEN cycles can be closed.
     * Requires Admin authorization.
     *
     * @param appraisalIds Comma-separated list of appraisal IDs
     * @return Response with count of closed appraisals
     */
    @POST
    @Path("/close-by-ids")
    public Response closeAppraisalsByIds(@QueryParam("ids") String appraisalIds) {
        try {
            // TODO: Add JWT Admin validation here
            // if (!isUserAdmin(jwtToken)) {
            // return Response.status(Response.Status.FORBIDDEN)
            // .entity(new ApiResponse(false, "Admin access required", "errorUnauthorized",
            // null))
            // .build();
            // }

            if (appraisalIds == null || appraisalIds.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse(false, "Appraisal IDs parameter is required", "errorMissingIds", null))
                        .build();
            }

            List<Long> ids = Arrays.stream(appraisalIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            LOGGER.info("Admin requesting to close appraisals by IDs: {}", ids);

            int closedCount = appraisalService.closeAppraisalsByIds(ids);

            String message = String.format("Successfully closed %d appraisal(s)", closedCount);
            return Response.ok(new ApiResponse(true, message, "success", closedCount))
                    .build();

        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid appraisal ID format: {}", appraisalIds);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid appraisal ID format", "errorInvalidIdFormat", null))
                    .build();
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid request for closing appraisals by IDs: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, e.getMessage(), "errorInvalidData", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error closing appraisals by IDs", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Closes all COMPLETED appraisals in a specific cycle.
     * Only works if the cycle is OPEN.
     * Requires Admin authorization.
     *
     * @param cycleId The cycle ID
     * @return Response with count of closed appraisals
     */
    @AdminOnly
    @POST
    @Path("/cycle/{cycleId}/close-completed")
    public Response closeCompletedAppraisalsByCycle(@PathParam("cycleId") Long cycleId) {
        try {
            LOGGER.info("Admin requesting to close COMPLETED appraisals in cycle ID: {}", cycleId);

            int closedCount = appraisalService.closeCompletedAppraisalsByCycleId(cycleId);

            String message = String.format("Successfully closed %d COMPLETED appraisal(s) in cycle %d", closedCount,
                    cycleId);
            return Response.ok(new ApiResponse(true, message, "success", closedCount))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Cycle not found with ID: {}", cycleId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorCycleNotFound", null))
                    .build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot close appraisals in cycle ID {}: {}", cycleId, e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiResponse(false, e.getMessage(), "errorCycleAlreadyClosed", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error closing appraisals in cycle ID: {}", cycleId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Closes all COMPLETED appraisals for a specific user.
     * Only works for appraisals in OPEN cycles.
     * Requires Admin authorization.
     *
     * @param userId The user ID
     * @return Response with count of closed appraisals
     */
    @POST
    @Path("/user/{userId}/close-completed")
    public Response closeCompletedAppraisalsByUser(@PathParam("userId") Long userId) {
        try {
            // TODO: Add JWT Admin validation here

            LOGGER.info("Admin requesting to close COMPLETED appraisals for user ID: {}", userId);

            int closedCount = appraisalService.closeCompletedAppraisalsByUserId(userId);

            String message = String.format("Successfully closed %d COMPLETED appraisal(s) for user %d", closedCount,
                    userId);
            return Response.ok(new ApiResponse(true, message, "success", closedCount))
                    .build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("User not found with ID: {}", userId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, e.getMessage(), "errorUserNotFound", null))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error closing appraisals for user ID: {}", userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Closes all COMPLETED appraisals in all OPEN cycles.
     * Administrative bulk operation.
     * Requires Admin authorization.
     *
     * @return Response with count of closed appraisals
     */
    @POST
    @Path("/close-all-completed")
    public Response closeAllCompletedAppraisals() {
        try {
            // TODO: Add JWT Admin validation here

            LOGGER.info("Admin requesting to close ALL COMPLETED appraisals");

            int closedCount = appraisalService.closeAllCompletedAppraisals();

            String message = String.format("Successfully closed %d COMPLETED appraisal(s) across all OPEN cycles",
                    closedCount);
            return Response.ok(new ApiResponse(true, message, "success", closedCount))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error closing all COMPLETED appraisals", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Exports filtered appraisals as a PDF file.
     *
     * @return PDF file with filtered appraisals
     */
    @GET
    @Path("/pdf")
    @Produces("application/pdf")
    public Response exportAppraisalsPdf(
            @QueryParam("appraisedUserId") Long appraisedUserId,
            @QueryParam("appraisedUserName") String appraisedUserName,
            @QueryParam("appraisedUserEmail") String appraisedUserEmail,
            @QueryParam("appraisingUserId") Long appraisingUserId,
            @QueryParam("appraisingUserName") String appraisingUserName,
            @QueryParam("appraisingUserEmail") String appraisingUserEmail,
            @QueryParam("language") @DefaultValue("ENGLISH") String languageStr,
            @QueryParam("cycleId") Long cycleId,
            @QueryParam("state") String appraisalStateStr,
            @QueryParam("parameter") @DefaultValue("creationDate") String parameterStr,
            @QueryParam("order") @DefaultValue("DESCENDING") String orderStr
    ) {
        try {
            AppraisalState state = appraisalStateStr != null
                    ? AppraisalState.valueOf(SearchUtils.normalizeString(appraisalStateStr))
                    : null;
            Language language = Language.fromFieldName(SearchUtils.normalizeString(languageStr));
            AppraisalParameter parameter = AppraisalParameter.fromFieldName(SearchUtils.normalizeString(parameterStr));
            OrderBy orderBy = OrderBy.fromFieldName(SearchUtils.normalizeString(orderStr));

            StreamingOutput stream = appraisalService.getPDFOfAppraisals(
                    appraisedUserId, appraisedUserName, appraisedUserEmail, appraisingUserId, appraisingUserName,
                    appraisingUserEmail, cycleId, state, parameter, orderBy, language);
            return Response.ok(stream)
                    .header("Content-Disposition", "attachment; filename=appraisals.pdf")
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error generating appraisals PDF", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Failed to generate PDF", "errorPdfExport", null))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    private Response validateUpdateAppraisalDTO(UpdateAppraisalDTO dto) {
        if (dto.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Appraisal ID is required", "errorMissingId", null))
                    .build();
        }
        if (dto.getFeedback() != null && dto.getFeedback().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Feedback must not be blank if provided", "errorFeedbackBlank",
                            null))
                    .build();
        }
        if (dto.getScore() != null) {
            int score = dto.getScore();
            if (score < 1 || score > 4) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse(false, "Score must be between 1 and 4 if provided",
                                "errorScoreOutOfBounds", null))
                        .build();
            }
        }
        if (dto.getState() == AppraisalState.COMPLETED) {
            String feedback = dto.getFeedback();
            if (feedback == null || feedback.trim().isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse(false, "Completed appraisal must have feedback", "errorFeedbackBlank",
                                null))
                        .build();
            }

            Integer score = dto.getScore();
            if (score == null || score < 1 || score > 4) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ApiResponse(false, "Completed appraisal must have a valid score between 1 and 4",
                                "errorScoreOutOfBounds", null))
                        .build();
            }
        }
        return null; // No validation issues
    }

    // Métodos helper para determinar códigos de erro
    private String getValidationErrorCode(String message) {
        if (message.contains("not found") && message.contains("User")) {
            return "errorUserNotFound";
        } else if (message.contains("Cycle") && message.contains("not found")) {
            return "errorCycleNotFound";
        } else if (message.contains("score") && (message.contains("between") || message.contains("invalid"))) {
            return "errorInvalidScore";
        } else if (message.contains("feedback") && message.contains("required")) {
            return "errorMissingFeedback";
        } else if (message.contains("Appraisal") && message.contains("not found")) {
            return "errorAppraisalNotFound";
        }
        return "errorInvalidData";
    }

    private String getConflictErrorCode(String message) {
        if (message.contains("already exists") || message.contains("duplicate")) {
            return "errorAppraisalExists";
        } else if (message.contains("already completed")) {
            return "errorAppraisalCompleted";
        } else if (message.contains("already closed")) {
            return "errorAppraisalClosed";
        } else if (message.contains("cycle") && message.contains("closed")) {
            return "errorCycleAlreadyClosed";
        }
        return "errorConflict";
    }
}
