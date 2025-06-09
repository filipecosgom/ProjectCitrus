package pt.uc.dei.utils;

public class ApiResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private Object data;

    public ApiResponse(boolean success, String message, String errorCode, Object data) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    // Getters and Setters
}