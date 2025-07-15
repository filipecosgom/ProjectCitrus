package pt.uc.dei.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.AppraisalState;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.services.UserService;
import pt.uc.dei.services.AppraisalService;
import pt.uc.dei.services.CourseService;
import pt.uc.dei.services.CycleService;
import pt.uc.dei.utils.ApiResponse;

import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock UserService userService;
    @Mock AppraisalService appraisalService;
    @Mock CourseService courseService;
    @Mock CycleService cycleService;
    @InjectMocks StatsController statsController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testGetUserStats() {
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("totalUsers", 10L);
        Map<String, Object> completeMap = new HashMap<>();
        completeMap.put("totalUsers", 7L);
        Map<String, Object> incompleteMap = new HashMap<>();
        incompleteMap.put("totalUsers", 3L);
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("totalUsers", 2L);
        Map<String, Object> managerMap = new HashMap<>();
        managerMap.put("totalUsers", 1L);
        when(userService.getUsers(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
            .thenReturn(totalMap)
            .thenReturn(completeMap)
            .thenReturn(incompleteMap)
            .thenReturn(adminMap)
            .thenReturn(managerMap);
        Response response = statsController.getUserStats();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        Map<String, Object> stats = (Map<String, Object>) api.getData();
        assertEquals(10L, stats.get("total"));
        assertNotNull(stats.get("profileCompletion"));
        assertNotNull(stats.get("roles"));
    }

    @Test
    void testGetAppraisalStats() {
        when(appraisalService.countAllAppraisals()).thenReturn(20L);
        when(appraisalService.countAppraisalsByState(AppraisalState.IN_PROGRESS)).thenReturn(5L);
        when(appraisalService.countAppraisalsByState(AppraisalState.COMPLETED)).thenReturn(10L);
        when(appraisalService.countAppraisalsByState(AppraisalState.CLOSED)).thenReturn(5L);
        Response response = statsController.getAppraisalStats();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        Map<String, Object> stats = (Map<String, Object>) api.getData();
        assertEquals(20L, stats.get("total"));
        assertEquals(5L, stats.get("inProgress"));
        assertEquals(10L, stats.get("completed"));
        assertEquals(5L, stats.get("closed"));
    }

    @Test
    void testGetCourseStats() {
        when(courseService.countAllCourses()).thenReturn(15L);
        when(courseService.countCoursesByActive(true)).thenReturn(12L);
        when(courseService.countCoursesByActive(false)).thenReturn(3L);
        Response response = statsController.getCourseStats();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        Map<String, Object> stats = (Map<String, Object>) api.getData();
        assertEquals(15L, stats.get("total"));
        assertEquals(12L, stats.get("active"));
        assertEquals(3L, stats.get("inactive"));
    }

    @Test
    void testGetCycleStats() {
        when(cycleService.countAllCycles()).thenReturn(8L);
        when(cycleService.countCyclesByState(CycleState.OPEN)).thenReturn(3L);
        when(cycleService.countCyclesByState(CycleState.CLOSED)).thenReturn(5L);
        Response response = statsController.getCycleStats();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        Map<String, Object> stats = (Map<String, Object>) api.getData();
        assertEquals(8L, stats.get("total"));
        assertEquals(3L, stats.get("open"));
        assertEquals(5L, stats.get("closed"));
    }
}
