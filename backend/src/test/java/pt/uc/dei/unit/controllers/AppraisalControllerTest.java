package pt.uc.dei.unit.controllers;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.uc.dei.controllers.AppraisalController;
import pt.uc.dei.dtos.*;
import pt.uc.dei.enums.*;
import pt.uc.dei.services.AppraisalService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.SearchUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppraisalControllerTest {
    @Mock AppraisalService appraisalService;
    @Mock UserService userService;
    @InjectMocks AppraisalController appraisalController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testUpdateAppraisal_success_admin() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(1L);
        dto.setState(AppraisalState.IN_PROGRESS);
        AppraisalDTO updated = new AppraisalDTO();
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenReturn(2L);
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            when(appraisalService.updateAppraisal(dto, true)).thenReturn(updated);
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
        }
    }

    @Test
    void testUpdateAppraisal_success_manager() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(1L);
        dto.setState(AppraisalState.IN_PROGRESS);
        AppraisalDTO updated = new AppraisalDTO();
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenReturn(2L);
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(false);
            when(appraisalService.checkIfManagerOfUser(1L, 2L)).thenReturn(true);
            when(appraisalService.updateAppraisal(dto, false)).thenReturn(updated);
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateAppraisal_forbidden_manager() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(1L);
        dto.setState(AppraisalState.IN_PROGRESS);
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenReturn(2L);
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(false);
            when(appraisalService.checkIfManagerOfUser(1L, 2L)).thenReturn(false);
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateAppraisal_validation_error() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(null);
        dto.setState(AppraisalState.IN_PROGRESS);
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenReturn(2L);
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(false);
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateAppraisal_illegalArgument() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(1L);
        dto.setState(AppraisalState.IN_PROGRESS);
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenReturn(2L);
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            when(appraisalService.updateAppraisal(dto, true)).thenThrow(new IllegalArgumentException("bad request"));
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateAppraisal_illegalState() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(1L);
        dto.setState(AppraisalState.IN_PROGRESS);
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenReturn(2L);
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            when(appraisalService.updateAppraisal(dto, true)).thenThrow(new IllegalStateException("conflict"));
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateAppraisal_unexpectedException() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(1L);
        dto.setState(AppraisalState.IN_PROGRESS);
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenReturn(2L);
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            when(appraisalService.updateAppraisal(dto, true)).thenThrow(new RuntimeException("fail"));
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateAppraisal_jwtValidationException() {
        UpdateAppraisalDTO dto = new UpdateAppraisalDTO();
        dto.setId(1L);
        dto.setState(AppraisalState.IN_PROGRESS);
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.extractUserIdOrAbort(anyString())).thenThrow(new JWTUtil.JwtValidationException("unauth"));
            jwt.when(() -> JWTUtil.buildUnauthorizedResponse(anyString())).thenReturn(Response.status(Response.Status.UNAUTHORIZED).build());
            Response response = appraisalController.updateAppraisal(dto, "jwt");
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetAppraisalById_found() {
        AppraisalDTO dto = new AppraisalDTO();
        when(appraisalService.getAppraisalById(1L)).thenReturn(dto);
        Response response = appraisalController.getAppraisalById(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalById_notFound() {
        when(appraisalService.getAppraisalById(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = appraisalController.getAppraisalById(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalById_exception() {
        when(appraisalService.getAppraisalById(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.getAppraisalById(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalsWithFilters_admin() {
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            Map<String, Object> data = new HashMap<>();
            when(appraisalService.getAppraisalsWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(data);
            Response response = appraisalController.getAppraisalsWithFilters(null, null, null, null, null, null, null, null, "creationDate", "DESCENDING", 10, 0, "jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetAppraisalsWithFilters_forbidden() {
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(false);
            jwt.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(2L);
            Response response = appraisalController.getAppraisalsWithFilters(1L, null, null, null, null, null, null, null, "creationDate", "DESCENDING", 10, 0, "jwt");
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetAppraisalsWithFilters_exception() {
        try (MockedStatic<JWTUtil> jwt = mockStatic(JWTUtil.class)) {
            jwt.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            when(appraisalService.getAppraisalsWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenThrow(new RuntimeException("fail"));
            Response response = appraisalController.getAppraisalsWithFilters(null, null, null, null, null, null, null, null, "creationDate", "DESCENDING", 10, 0, "jwt");
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetAppraisalsByUser_success() {
        List<AppraisalDTO> list = new ArrayList<>();
        when(appraisalService.getAppraisalsByAppraisedUser(1L)).thenReturn(list);
        Response response = appraisalController.getAppraisalsByUser(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalsByUser_exception() {
        when(appraisalService.getAppraisalsByAppraisedUser(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.getAppraisalsByUser(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalsByManager_success() {
        List<AppraisalDTO> list = new ArrayList<>();
        when(appraisalService.getAppraisalsByManager(1L)).thenReturn(list);
        Response response = appraisalController.getAppraisalsByManager(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalsByManager_exception() {
        when(appraisalService.getAppraisalsByManager(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.getAppraisalsByManager(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalsByCycle_success() {
        List<AppraisalDTO> list = new ArrayList<>();
        when(appraisalService.getAppraisalsByCycle(1L)).thenReturn(list);
        Response response = appraisalController.getAppraisalsByCycle(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalsByCycle_exception() {
        when(appraisalService.getAppraisalsByCycle(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.getAppraisalsByCycle(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testCompleteAppraisal_success() {
        AppraisalDTO dto = new AppraisalDTO();
        when(appraisalService.completeAppraisal(1L)).thenReturn(dto);
        Response response = appraisalController.completeAppraisal(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCompleteAppraisal_notFound() {
        when(appraisalService.completeAppraisal(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = appraisalController.completeAppraisal(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testCompleteAppraisal_conflict() {
        when(appraisalService.completeAppraisal(1L)).thenThrow(new IllegalStateException("conflict"));
        Response response = appraisalController.completeAppraisal(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testCompleteAppraisal_exception() {
        when(appraisalService.completeAppraisal(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.completeAppraisal(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisal_success() {
        AppraisalDTO dto = new AppraisalDTO();
        when(appraisalService.closeAppraisal(1L)).thenReturn(dto);
        Response response = appraisalController.closeAppraisal(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisal_notFound() {
        when(appraisalService.closeAppraisal(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = appraisalController.closeAppraisal(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisal_conflict() {
        when(appraisalService.closeAppraisal(1L)).thenThrow(new IllegalStateException("conflict"));
        Response response = appraisalController.closeAppraisal(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisal_exception() {
        when(appraisalService.closeAppraisal(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.closeAppraisal(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteAppraisal_success() {
        Response response = appraisalController.deleteAppraisal(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteAppraisal_notFound() {
        doThrow(new IllegalArgumentException("not found")).when(appraisalService).deleteAppraisal(1L);
        Response response = appraisalController.deleteAppraisal(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteAppraisal_conflict() {
        doThrow(new IllegalStateException("conflict")).when(appraisalService).deleteAppraisal(1L);
        Response response = appraisalController.deleteAppraisal(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testDeleteAppraisal_exception() {
        doThrow(new RuntimeException("fail")).when(appraisalService).deleteAppraisal(1L);
        Response response = appraisalController.deleteAppraisal(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalStats_success() {
        AppraisalStatsDTO stats = new AppraisalStatsDTO();
        when(appraisalService.getAppraisalStats(1L)).thenReturn(stats);
        Response response = appraisalController.getAppraisalStats(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAppraisalStats_exception() {
        when(appraisalService.getAppraisalStats(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.getAppraisalStats(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisalsByIds_success() {
        when(appraisalService.closeAppraisalsByIds(anyList())).thenReturn(2);
        Response response = appraisalController.closeAppraisalsByIds("1,2");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisalsByIds_missingIds() {
        Response response = appraisalController.closeAppraisalsByIds("");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisalsByIds_invalidFormat() {
        Response response = appraisalController.closeAppraisalsByIds("abc");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisalsByIds_illegalArgument() {
        when(appraisalService.closeAppraisalsByIds(anyList())).thenThrow(new IllegalArgumentException("bad data"));
        Response response = appraisalController.closeAppraisalsByIds("1,2");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAppraisalsByIds_exception() {
        when(appraisalService.closeAppraisalsByIds(anyList())).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.closeAppraisalsByIds("1,2");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCompletedAppraisalsByCycle_success() {
        when(appraisalService.closeCompletedAppraisalsByCycleId(1L)).thenReturn(3);
        Response response = appraisalController.closeCompletedAppraisalsByCycle(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCompletedAppraisalsByCycle_notFound() {
        when(appraisalService.closeCompletedAppraisalsByCycleId(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = appraisalController.closeCompletedAppraisalsByCycle(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCompletedAppraisalsByCycle_conflict() {
        when(appraisalService.closeCompletedAppraisalsByCycleId(1L)).thenThrow(new IllegalStateException("closed"));
        Response response = appraisalController.closeCompletedAppraisalsByCycle(1L);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCompletedAppraisalsByCycle_exception() {
        when(appraisalService.closeCompletedAppraisalsByCycleId(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.closeCompletedAppraisalsByCycle(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCompletedAppraisalsByUser_success() {
        when(appraisalService.closeCompletedAppraisalsByUserId(1L)).thenReturn(2);
        Response response = appraisalController.closeCompletedAppraisalsByUser(1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCompletedAppraisalsByUser_notFound() {
        when(appraisalService.closeCompletedAppraisalsByUserId(1L)).thenThrow(new IllegalArgumentException("not found"));
        Response response = appraisalController.closeCompletedAppraisalsByUser(1L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseCompletedAppraisalsByUser_exception() {
        when(appraisalService.closeCompletedAppraisalsByUserId(1L)).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.closeCompletedAppraisalsByUser(1L);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAllCompletedAppraisals_success() {
        when(appraisalService.closeAllCompletedAppraisals()).thenReturn(5);
        Response response = appraisalController.closeAllCompletedAppraisals();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testCloseAllCompletedAppraisals_exception() {
        when(appraisalService.closeAllCompletedAppraisals()).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.closeAllCompletedAppraisals();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testExportAppraisalsPdf_success() {
        InputStream pdfStream = new ByteArrayInputStream(new byte[]{1,2,3});
        when(appraisalService.getPDFOfAppraisals(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(outputStream -> outputStream.write(pdfStream.readAllBytes()));
        Response response = appraisalController.exportAppraisalsPdf(null, null, null, null, null, null, "en", null, null, "creationDate", "DESCENDING");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("attachment; filename=appraisals.pdf", response.getHeaderString("Content-Disposition"));
    }

    @Test
    void testExportAppraisalsPdf_exception() {
        when(appraisalService.getPDFOfAppraisals(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenThrow(new RuntimeException("fail"));
        Response response = appraisalController.exportAppraisalsPdf(null, null, null, null, null, null, "en", null, null, "creationDate", "DESCENDING");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
