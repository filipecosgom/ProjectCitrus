package pt.uc.dei.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import pt.uc.dei.controllers.CourseController;
import pt.uc.dei.dtos.*;
import pt.uc.dei.enums.*;
import pt.uc.dei.services.CourseService;
import pt.uc.dei.services.CourseFileService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.SearchUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {
    @Mock CourseService courseService;
    @InjectMocks CourseController courseController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testGetCoursesWithFilters_success() {
        Map<String, Object> data = new HashMap<>();
        when(courseService.getCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any())).thenReturn(data);
        // Use default values for parameterStr and orderStr to avoid NullPointerException
        Response response = courseController.getCoursesWithFilters(null, null, null, null, null, null, null, null, null, null, "title", "ASCENDING", 0, 10);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals(data, api.getData());
    }

    @Test
    void testGetCoursesWithFilters_internalError() {
        when(courseService.getCoursesWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any())).thenThrow(new RuntimeException("fail"));
        // Use default values for parameterStr and orderStr to avoid NullPointerException
        Response response = courseController.getCoursesWithFilters(null, null, null, null, null, null, null, null, null, null, "title", "ASCENDING", 0, 10);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetCourseImage_notFound() {
        try (MockedStatic<CourseFileService> mocked = mockStatic(CourseFileService.class)) {
            mocked.when(() -> CourseFileService.resolveCourseImagePath(anyLong())).thenReturn(null);
            Response response = courseController.getCourseImage(1L, mock(Request.class), mock(HttpHeaders.class));
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetCourseImage_streamError() throws IOException {
        Path fakePath = mock(Path.class);
        long lastModified = System.currentTimeMillis();
        long fileSize = 123L;
        String mimeType = "image/png";
        CourseFileService.CacheData cacheData = new CourseFileService.CacheData(lastModified, fileSize, mimeType);
        try (MockedStatic<CourseFileService> mocked = mockStatic(CourseFileService.class)) {
            mocked.when(() -> CourseFileService.resolveCourseImagePath(anyLong())).thenReturn(fakePath);
            mocked.when(() -> CourseFileService.getCacheData(fakePath)).thenReturn(cacheData);
            Request req = mock(Request.class);
            when(req.evaluatePreconditions(any(Date.class), any())).thenReturn(null);
            HttpHeaders headers = mock(HttpHeaders.class);
            when(headers.getAcceptableMediaTypes()).thenReturn(List.of(jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE));
            // The actual streaming error is handled in the StreamingOutput, which is not directly testable here
            // So we just check that the response is OK and type is set
            Response response = courseController.getCourseImage(1L, req, headers);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUploadCourseImage_success() {
        FileUploadDTO form = mock(FileUploadDTO.class);
        InputStream stream = new ByteArrayInputStream(new byte[]{1,2,3});
        when(form.getFileStream()).thenReturn(stream);
        try (MockedStatic<CourseFileService> mocked = mockStatic(CourseFileService.class)) {
            mocked.when(() -> CourseFileService.getFilename(anyLong(), any())).thenReturn("file.png");
            mocked.when(() -> CourseFileService.isValidMimeType(any())).thenReturn(true);
            mocked.when(() -> CourseFileService.saveFileWithSizeLimit(any(), anyString())).thenReturn(true);
            Response response = courseController.uploadCourseImage(1L, form);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUploadCourseImage_invalidType() {
        FileUploadDTO form = mock(FileUploadDTO.class);
        InputStream stream = new ByteArrayInputStream(new byte[]{1,2,3});
        when(form.getFileStream()).thenReturn(stream);
        try (MockedStatic<CourseFileService> mocked = mockStatic(CourseFileService.class)) {
            mocked.when(() -> CourseFileService.getFilename(anyLong(), any())).thenReturn("file.png");
            mocked.when(() -> CourseFileService.isValidMimeType(any())).thenReturn(false);
            Response response = courseController.uploadCourseImage(1L, form);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUploadCourseImage_fileTooLarge() {
        FileUploadDTO form = mock(FileUploadDTO.class);
        InputStream stream = new ByteArrayInputStream(new byte[]{1,2,3});
        when(form.getFileStream()).thenReturn(stream);
        try (MockedStatic<CourseFileService> mocked = mockStatic(CourseFileService.class)) {
            mocked.when(() -> CourseFileService.getFilename(anyLong(), any())).thenReturn("file.png");
            mocked.when(() -> CourseFileService.isValidMimeType(any())).thenReturn(true);
            mocked.when(() -> CourseFileService.saveFileWithSizeLimit(any(), anyString())).thenReturn(false);
            Response response = courseController.uploadCourseImage(1L, form);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUploadCourseImage_streamError() {
        FileUploadDTO form = mock(FileUploadDTO.class);
        InputStream stream = mock(InputStream.class);
        when(form.getFileStream()).thenReturn(stream);
        try {
            doThrow(new IOException("fail")).when(stream).transferTo(any());
        } catch (IOException e) { /* ignore */ }
        Response response = courseController.uploadCourseImage(1L, form);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testCreateCourse_unauthorized() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(false);
            Response response = courseController.createCourse("jwt", new CourseNewDTO());
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testCreateCourse_success() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            CourseNewDTO dto = new CourseNewDTO();
            CourseDTO created = new CourseDTO();
            when(courseService.createNewCourse(dto, 1L)).thenReturn(created);
            Response response = courseController.createCourse("jwt", dto);
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertEquals(created, api.getData());
        }
    }

    @Test
    void testCreateCourse_duplicateTitle() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            CourseNewDTO dto = new CourseNewDTO();
            when(courseService.createNewCourse(dto, 1L)).thenThrow(new IllegalArgumentException("duplicateTitle"));
            Response response = courseController.createCourse("jwt", dto);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testCreateCourse_duplicateLink() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            CourseNewDTO dto = new CourseNewDTO();
            when(courseService.createNewCourse(dto, 1L)).thenThrow(new IllegalArgumentException("duplicateLink"));
            Response response = courseController.createCourse("jwt", dto);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testCreateCourse_internalError() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            CourseNewDTO dto = new CourseNewDTO();
            when(courseService.createNewCourse(dto, 1L)).thenThrow(new RuntimeException("fail"));
            Response response = courseController.createCourse("jwt", dto);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateCourse_unauthorized() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(false);
            Response response = courseController.updateCourse("jwt", 1L, new CourseUpdateDTO());
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateCourse_missingData() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            Response response = courseController.updateCourse("jwt", 1L, null);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateCourse_success() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            CourseUpdateDTO dto = new CourseUpdateDTO();
            dto.setId(1L);
            when(courseService.updateCourse(dto)).thenReturn(true);
            Response response = courseController.updateCourse("jwt", 1L, dto);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateCourse_notFound() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            CourseUpdateDTO dto = new CourseUpdateDTO();
            dto.setId(1L);
            when(courseService.updateCourse(dto)).thenReturn(false);
            Response response = courseController.updateCourse("jwt", 1L, dto);
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateCourse_duplicateTitle() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            CourseUpdateDTO dto = new CourseUpdateDTO();
            dto.setId(1L);
            when(courseService.updateCourse(dto)).thenThrow(new IllegalArgumentException("duplicateTitle"));
            Response response = courseController.updateCourse("jwt", 1L, dto);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateCourse_duplicateLink() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            CourseUpdateDTO dto = new CourseUpdateDTO();
            dto.setId(1L);
            when(courseService.updateCourse(dto)).thenThrow(new IllegalArgumentException("duplicateLink"));
            Response response = courseController.updateCourse("jwt", 1L, dto);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateCourse_internalError() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.isUserAdmin(anyString())).thenReturn(true);
            CourseUpdateDTO dto = new CourseUpdateDTO();
            dto.setId(1L);
            when(courseService.updateCourse(dto)).thenThrow(new RuntimeException("fail"));
            Response response = courseController.updateCourse("jwt", 1L, dto);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }
}
