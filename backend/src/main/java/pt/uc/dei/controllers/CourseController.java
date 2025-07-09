package pt.uc.dei.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import pt.uc.dei.annotations.AdminOnly;
import pt.uc.dei.dtos.FileUploadDTO;
import pt.uc.dei.enums.*;
import pt.uc.dei.services.CourseService;
import pt.uc.dei.services.CourseFileService;
import pt.uc.dei.utils.ApiResponse;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Map;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.StreamingOutput;
import pt.uc.dei.utils.SearchUtils;

/**
 * REST Controller for managing course-related operations.
 * <p>
 * Provides endpoints for course retrieval and filtering functionality.
 */
@Path("/courses")
public class CourseController {

    /**
     * Logger instance for logging operations within this controller.
     */
    private static final Logger LOGGER = LogManager.getLogger(CourseController.class);

    @EJB
    private CourseService courseService;

    /**
     * Retrieves courses with filtering options.
     *
     * @param areaStr     Optional filter by course area
     * @param languageStr Optional filter by course language
     * @param adminName  Optional filter by admin name
     * @param courseIsActive Optional filter by active status
     * @param limit    Maximum number of results
     * @param offset   Starting position for pagination
     * @return Response with list of filtered course DTOs
     */
    @AdminOnly
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCoursesWithFilters(
            @QueryParam("id") Long id,
            @QueryParam("title") String title,
            @QueryParam("duration") Integer duration,
            @QueryParam("description") String description,
            @QueryParam("area") String areaStr,
            @QueryParam("language") String languageStr,
            @QueryParam("adminName") String adminName,
            @QueryParam("courseIsActive") Boolean courseIsActive,
            @QueryParam("parameter") @DefaultValue("title") String parameterStr,
            @QueryParam("order") @DefaultValue("ASCENDING") String orderStr,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("limit") @DefaultValue("10") Integer limit) {
        Language language = languageStr != null ? Language.fromFieldName(languageStr) : null;
        CourseArea area = areaStr != null ? CourseArea.fromFieldName(SearchUtils.normalizeString(areaStr)) : null;
        CourseParameter parameter = CourseParameter.fromFieldName(SearchUtils.normalizeString(parameterStr));
        OrderBy orderBy = OrderBy.fromFieldName(SearchUtils.normalizeString(orderStr));
        try {
            LOGGER.debug("Retrieving courses with filters");
            Map<String, Object> courseData = courseService.getCoursesWithFilters(
                    id, title, duration, description, area, language, adminName, courseIsActive,
                    parameter, orderBy, offset, limit);
            return Response.ok(new ApiResponse(true, "Courses retrieved successfully", "success", courseData))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Unexpected error retrieving courses with filters", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternalServer", null))
                    .build();
        }
    }

    /**
     * Retrieves the image for a course by ID.
     *
     * @param id      Course ID.
     * @param request HTTP request context.
     * @param headers HTTP headers.
     * @return The course image or error response.
     */
    @GET
    @Path("/{id}/image")
    @Produces({ MediaType.APPLICATION_JSON, "image/jpeg", "image/png", "image/webp" })
    public Response getCourseImage(
            @PathParam("id") Long id,
            @Context Request request,
            @Context HttpHeaders headers) {
        LOGGER.info("Course image retrieval request for course id: {}", id);
        try {
            java.nio.file.Path imagePath = CourseFileService.resolveCourseImagePath(id);
            if (imagePath == null) {
                LOGGER.warn("Image not found for course id: {}", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(new ApiResponse(false, "Image not found", "courseImageNotFound", null))
                        .build();
            }

            CourseFileService.CacheData cacheData = CourseFileService.getCacheData(imagePath);
            EntityTag etag = new EntityTag(cacheData.lastModified + "-" + cacheData.fileSize);

            Response.ResponseBuilder builder = request.evaluatePreconditions(
                    new Date(cacheData.lastModified),
                    etag);

            if (builder != null) {
                LOGGER.info("Course image not modified for course id: {}", id);
                return builder.build();
            }

            boolean acceptsJson = headers.getAcceptableMediaTypes().stream()
                    .anyMatch(m -> m.isCompatible(MediaType.APPLICATION_JSON_TYPE));

            StreamingOutput stream = output -> {
                try (InputStream in = Files.newInputStream(imagePath)) {
                    in.transferTo(output);
                } catch (IOException e) {
                    LOGGER.error("Streaming error for course image of id: {}", id, e);
                    if (acceptsJson) {
                        output.write(new ObjectMapper().writeValueAsBytes(
                                new ApiResponse(false, "Stream error", "streamError", null)));
                    }
                }
            };
            LOGGER.info("Course image returned for course id: {}", id);
            return Response.ok(stream)
                    .type(cacheData.mimeType)
                    .header("Cache-Control", "public, max-age=86400")
                    .header("ETag", etag.toString())
                    .lastModified(new Date(cacheData.lastModified))
                    .build();

        } catch (IOException e) {
            LOGGER.error("Failed to load course image for id: {}", id, e);
            return Response.serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ApiResponse(false, "Failed to load image", "courseImageLoadError", null))
                    .build();
        }
    }

    /**
     * Uploads or updates a course's image.
     *
     * @param id   Course ID.
     * @param form Multipart form containing the image file.
     * @return HTTP 200 (OK) if uploaded, or error response.
     */
    @AdminOnly
    @PUT
    @Path("/{id}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadCourseImage(@PathParam("id") Long id, @MultipartForm FileUploadDTO form) {
        LOGGER.info("Course image upload request for course id: {}", id);
        InputStream imageStream = form.getFileStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            imageStream.transferTo(baos);
        } catch (Exception e) {
            LOGGER.error("Failed to read course image file for course id: {}", id, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Unsupported file type", "errorInvalidType", null))
                    .build();
        }
        byte[] fileBytes = baos.toByteArray();
        InputStream forMimeType = new ByteArrayInputStream(fileBytes);
        InputStream forSaving = new ByteArrayInputStream(fileBytes);
        String filename = CourseFileService.getFilename(id, fileBytes);
        if (!CourseFileService.isValidMimeType(forMimeType)) {
            LOGGER.warn("Invalid mime type for course image upload by course id: {}", id);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Unsupported file type", "errorInvalidType", null))
                    .build();
        }
        CourseFileService.removeExistingCourseImages(id);
        boolean saved = CourseFileService.saveFileWithSizeLimit(forSaving, filename);
        if (!saved) {
            LOGGER.warn("File too large for course image upload by course id: {}", id);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "File too large", "errorFileTooLarge", null))
                    .build();
        }
        // Optionally update course entity to set courseHasImage = true
        LOGGER.info("Course image uploaded successfully for course id: {}", id);
        return Response.status(Response.Status.OK)
                .entity(new ApiResponse(true, "File uploaded successfully", "successFileUploaded", filename))
                .build();
    }
}
