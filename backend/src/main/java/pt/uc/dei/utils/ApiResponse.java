package pt.uc.dei.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard API response wrapper for REST endpoints.
 * <p>
 * Encapsulates success status, message, error code, and optional data payload.
 * Used to provide consistent response structure for API clients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private Object data;

    /**
     * Constructs an ApiResponse with the given parameters.
     *
     * @param success whether the operation was successful
     * @param message a message describing the result
     * @param errorCode an optional error code (can be null)
     * @param data optional data payload (can be null)
     */
    public ApiResponse(boolean success, String message, String errorCode, Object data) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    /**
     * Returns whether the operation was successful.
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the response.
     * @param success true if successful, false otherwise
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the message describing the result.
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message describing the result.
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the error code, if any.
     * @return the error code, or null if none
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     * @param errorCode the error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the data payload, if any.
     * @return the data payload, or null if none
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the data payload.
     * @param data the data payload
     */
    public void setData(Object data) {
        this.data = data;
    }
}