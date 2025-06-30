package pt.uc.dei.controllers;

import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.Role;
import pt.uc.dei.enums.Office;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/enums")
@Produces(MediaType.APPLICATION_JSON)
public class EnumController {

    /**
     * Logger for tracking enum requests.
     */
    private static final Logger LOGGER = LogManager.getLogger(EnumController.class);

    /**
     * Retrieves all available user roles.
     *
     * @return HTTP 200 (OK) with a list of role names.
     */
    @GET
    @Path("/roles")
    public Response getRoles() {
        LOGGER.info("Request received for all roles");
        List<String> roles = Arrays.stream(Role.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        LOGGER.info("Returning {} roles", roles.size());
        return Response.ok(roles).build();
    }

    /**
     * Retrieves all available offices.
     *
     * @return HTTP 200 (OK) with a list of office names.
     */
    @GET
    @Path("/offices")
    public Response getOffices() {
        LOGGER.info("Request received for all offices");
        List<String> offices = Arrays.stream(Office.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        LOGGER.info("Returning {} offices", offices.size());
        return Response.ok(offices).build();
    }

    @GET
    @Path("/appraisalStates")
    public Response getAppraisalStates() {
        LOGGER.info("Request received for all appraisal states");
        List<String> appraisals = Arrays.stream(AppraisalState.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        LOGGER.info("Returning {} appraisal states", appraisals.size());
        return Response.ok(appraisals).build();
    }
}