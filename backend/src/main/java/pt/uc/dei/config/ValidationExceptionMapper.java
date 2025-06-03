package pt.uc.dei.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper for handling validation errors.
 * Converts {@link ConstraintViolationException} instances into HTTP responses with appropriate error messages.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /**
     * Converts a {@link ConstraintViolationException} into an HTTP response.
     * Extracts the validation error message from the exception and returns a 400 Bad Request response.
     *
     * @param exception The exception containing validation constraint violations.
     * @return A {@link Response} object with the validation error message and a 400 status code.
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String errorMessage = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Invalid request data");

        return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
    }
}