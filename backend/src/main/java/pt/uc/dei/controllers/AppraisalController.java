package pt.uc.dei.controllers;

import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.AppraisalDTO;
import pt.uc.dei.dtos.CreateAppraisalDTO;
import pt.uc.dei.dtos.UpdateAppraisalDTO;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.services.AppraisalService;

import java.util.List;

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
     * Creates a new appraisal.
     *
     * @param createAppraisalDTO The appraisal creation data
     * @return Response with the created appraisal DTO
     */
    @POST
    public Response createAppraisal(@Valid CreateAppraisalDTO createAppraisalDTO) {
        try {
            LOGGER.info("Creating new appraisal for user {} by user {}", 
                       createAppraisalDTO.getAppraisedUserId(), 
                       createAppraisalDTO.getAppraisingUserId());

            AppraisalDTO createdAppraisal = appraisalService.createAppraisal(createAppraisalDTO);
            return Response.status(Response.Status.CREATED).entity(createdAppraisal).build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid request for appraisal creation: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Business rule violation for appraisal creation: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error creating appraisal", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
        }
    }

    /**
     * Updates an existing appraisal.
     *
     * @param updateAppraisalDTO The appraisal update data
     * @return Response with the updated appraisal DTO
     */
    @PUT
    public Response updateAppraisal(@Valid UpdateAppraisalDTO updateAppraisalDTO) {
        try {
            LOGGER.info("Updating appraisal with ID: {}", updateAppraisalDTO.getId());

            AppraisalDTO updatedAppraisal = appraisalService.updateAppraisal(updateAppraisalDTO);
            return Response.ok(updatedAppraisal).build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid request for appraisal update: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Business rule violation for appraisal update: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error updating appraisal", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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
            return Response.ok(appraisal).build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
        }
    }

    /**
     * Retrieves appraisals with filtering options.
     *
     * @param appraisedUserId Optional filter by appraised user ID
     * @param appraisingUserId Optional filter by appraising user ID (manager)
     * @param cycleId Optional filter by cycle ID
     * @param state Optional filter by appraisal state
     * @param limit Maximum number of results
     * @param offset Starting position for pagination
     * @return Response with list of filtered appraisal DTOs
     */
    @GET
    public Response getAppraisalsWithFilters(@QueryParam("appraisedUserId") Long appraisedUserId,
                                           @QueryParam("appraisingUserId") Long appraisingUserId,
                                           @QueryParam("cycleId") Long cycleId,
                                           @QueryParam("state") AppraisalState state,
                                           @QueryParam("limit") @DefaultValue("50") Integer limit,
                                           @QueryParam("offset") @DefaultValue("0") Integer offset) {
        try {
            LOGGER.debug("Retrieving appraisals with filters");

            List<AppraisalDTO> appraisals = appraisalService.getAppraisalsWithFilters(
                appraisedUserId, appraisingUserId, cycleId, state, limit, offset
            );
            return Response.ok(appraisals).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals with filters", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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
            return Response.ok(appraisals).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals for user ID: {}", userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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
            return Response.ok(appraisals).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals for manager ID: {}", managerId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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
            return Response.ok(appraisals).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving appraisals for cycle ID: {}", cycleId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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
            return Response.ok(completedAppraisal).build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot complete appraisal with ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error completing appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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
            return Response.ok(closedAppraisal).build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error closing appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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
            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            LOGGER.warn("Appraisal not found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalStateException e) {
            LOGGER.warn("Cannot delete appraisal with ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                          .entity(new ErrorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error deleting appraisal with ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
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

            AppraisalService.AppraisalStatsDTO stats = appraisalService.getAppraisalStats(userId);
            return Response.ok(stats).build();

        } catch (Exception e) {
            LOGGER.error("Unexpected error getting appraisal statistics for user ID: {}", userId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(new ErrorResponse("Internal server error")).build();
        }
    }

    /**
     * Error response wrapper class.
     */
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
