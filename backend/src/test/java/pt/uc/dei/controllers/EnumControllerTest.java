package pt.uc.dei.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.CourseArea;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Role;

import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EnumControllerTest {
    @InjectMocks EnumController enumController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testGetRoles() {
        Response response = enumController.getRoles();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<String> roles = (List<String>) response.getEntity();
        assertNotNull(roles);
        assertIterableEquals(
            Arrays.stream(Role.values()).map(Enum::name).toList(),
            roles
        );
    }

    @Test
    void testGetOffices() {
        Response response = enumController.getOffices();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<String> offices = (List<String>) response.getEntity();
        assertNotNull(offices);
        assertIterableEquals(
            Arrays.stream(Office.values()).map(Enum::name).toList(),
            offices
        );
    }

    @Test
    void testGetAppraisalStates() {
        Response response = enumController.getAppraisalStates();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<String> states = (List<String>) response.getEntity();
        assertNotNull(states);
        assertIterableEquals(
            Arrays.stream(AppraisalState.values()).map(Enum::name).toList(),
            states
        );
    }

    @Test
    void testGetCourseAreas() {
        Response response = enumController.getCourseAreas();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<String> areas = (List<String>) response.getEntity();
        assertNotNull(areas);
        assertIterableEquals(
            Arrays.stream(CourseArea.values()).map(Enum::name).toList(),
            areas
        );
    }
}
