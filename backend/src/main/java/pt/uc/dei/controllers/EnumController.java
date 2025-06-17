package pt.uc.dei.controllers;

import pt.uc.dei.enums.Role;
import pt.uc.dei.enums.Office;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/enums")
@Produces(MediaType.APPLICATION_JSON)
public class EnumController {

    @GET
    @Path("/roles")
    public Response getRoles() {
        List<String> roles = Arrays.stream(Role.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return Response.ok(roles).build();
    }

    @GET
    @Path("/offices")
    public Response getOffices() {
        List<String> offices = Arrays.stream(Office.values())
            .map(Enum::name)
            .collect(Collectors.toList());
        return Response.ok(offices).build();
    }
}
