package pt.uc.dei.unit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.uc.dei.controllers.CycleController;
import pt.uc.dei.dtos.CycleDTO;
import pt.uc.dei.dtos.CycleUpdateDTO;
import pt.uc.dei.enums.CycleState;
import pt.uc.dei.services.CycleService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;

import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CycleControllerTest {
    @Mock CycleService cycleService;
    @InjectMocks CycleController cycleController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testCreateCycle_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            CycleDTO dto = new CycleDTO();
            dto.setStartDate(LocalDate.now());
            dto.setEndDate(LocalDate.now().plusDays(1));
            CycleDTO created = new CycleDTO();
            when(cycleService.createCycle(any())).thenReturn(created);
            Response response = cycleController.createCycle(dto, "jwt");
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertEquals(created, api.getData());
        }
    }

    @Test
    void testCreateCycle_illegalArgument() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            CycleDTO dto = new CycleDTO();
            when(cycleService.createCycle(any())).thenThrow(new IllegalArgumentException("bad data"));
            Response response = cycleController.createCycle(dto, "jwt");
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testCreateCycle_illegalState() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            CycleDTO dto = new CycleDTO();
            when(cycleService.createCycle(any())).thenThrow(new IllegalStateException("conflict"));
            Response response = cycleController.createCycle(dto, "jwt");
            assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetCycleById_success() {
        CycleDTO dto = new CycleDTO();
        when(cycleService.getCycleById(1L)).thenReturn(dto);
        Response response = cycleController.getCycleById(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(dto, api.getData());
    }

    @Test
    void testGetCycleById_notFound() {
        when(cycleService.getCycleById(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = cycleController.getCycleById(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetCyclesWithFilters_all() {
        Map<String, Object> data = new HashMap<>();
        when(cycleService.getAllCycles(anyInt(), anyInt())).thenReturn(data);
        Response response = cycleController.getCyclesWithFilters(null, null, null, null, 10, 0);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(data, api.getData());
    }

    @Test
    void testGetCyclesWithFilters_filtered() {
        Map<String, Object> data = new HashMap<>();
        when(cycleService.getCyclesWithFilters(any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(data);
        Response response = cycleController.getCyclesWithFilters(CycleState.OPEN, 1L, "2025-01-01", "2025-12-31", 10, 0);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(data, api.getData());
    }

    @Test
    void testGetCyclesWithFilters_invalidDate() {
        Response response = cycleController.getCyclesWithFilters(null, null, "bad-date", null, 10, 0);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetCurrentActiveCycle_success() {
        CycleDTO dto = new CycleDTO();
        when(cycleService.getCurrentActiveCycle()).thenReturn(dto);
        Response response = cycleController.getCurrentActiveCycle();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(dto, api.getData());
    }

    @Test
    void testGetCurrentActiveCycle_notFound() {
        when(cycleService.getCurrentActiveCycle()).thenReturn(null);
        Response response = cycleController.getCurrentActiveCycle();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetUpcomingCycles_success() {
        List<CycleDTO> list = List.of(new CycleDTO());
        when(cycleService.getUpcomingCycles()).thenReturn(list);
        Response response = cycleController.getUpcomingCycles();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(list, api.getData());
    }

    @Test
    void testGetCyclesByAdmin_success() {
        List<CycleDTO> list = List.of(new CycleDTO());
        when(cycleService.getCyclesByAdmin(1L)).thenReturn(list);
        Response response = cycleController.getCyclesByAdmin(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(list, api.getData());
    }

    @Test
    void testCloseCycle_success() {
        CycleDTO dto = new CycleDTO();
        when(cycleService.closeCycle(1L)).thenReturn(dto);
        Response response = cycleController.closeCycle(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(dto, api.getData());
    }

    @Test
    void testCloseCycle_notFound() {
        when(cycleService.closeCycle(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = cycleController.closeCycle(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCycle_conflict() {
        when(cycleService.closeCycle(1L)).thenThrow(new IllegalStateException("already closed"));
        Response response = cycleController.closeCycle(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testReopenCycle_success() {
        CycleDTO dto = new CycleDTO();
        when(cycleService.reopenCycle(1L)).thenReturn(dto);
        Response response = cycleController.reopenCycle(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(dto, api.getData());
    }

    @Test
    void testReopenCycle_notFound() {
        when(cycleService.reopenCycle(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = cycleController.reopenCycle(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testReopenCycle_conflict() {
        when(cycleService.reopenCycle(1L)).thenThrow(new IllegalStateException("already open"));
        Response response = cycleController.reopenCycle(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteCycle_success() {
        Response response = cycleController.deleteCycle(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
    }

    @Test
    void testDeleteCycle_notFound() {
        doThrow(new IllegalArgumentException("not found")).when(cycleService).deleteCycle(1L);
        Response response = cycleController.deleteCycle(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteCycle_conflict() {
        doThrow(new IllegalStateException("already closed")).when(cycleService).deleteCycle(1L);
        Response response = cycleController.deleteCycle(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseExpiredCycles_success() {
        when(cycleService.closeExpiredCycles()).thenReturn(3);
        Response response = cycleController.closeExpiredCycles();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(3, api.getData());
    }

    @Test
    void testCanCloseCycle_success() {
        Map<String, Object> validation = new HashMap<>();
        when(cycleService.canCloseCycle(1L)).thenReturn(validation);
        Response response = cycleController.canCloseCycle(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(validation, api.getData());
    }

    @Test
    void testCanCloseCycle_notFound() {
        when(cycleService.canCloseCycle(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = cycleController.canCloseCycle(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testCanCloseCycle_conflict() {
        when(cycleService.canCloseCycle(1L)).thenThrow(new IllegalStateException("already closed"));
        Response response = cycleController.canCloseCycle(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }
}
