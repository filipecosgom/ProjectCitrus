package pt.uc.dei.unit.utils;

import pt.uc.dei.utils.ApiResponse;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {
    @Test
    void testConstructorAndGetters() {
        ApiResponse response = new ApiResponse(true, "Success", null, "data");
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertNull(response.getErrorCode());
        assertEquals("data", response.getData());
    }

    @Test
    void testSetters() {
        ApiResponse response = new ApiResponse(false, null, null, null);
        response.setSuccess(true);
        response.setMessage("msg");
        response.setErrorCode("E1");
        response.setData(123);
        assertTrue(response.isSuccess());
        assertEquals("msg", response.getMessage());
        assertEquals("E1", response.getErrorCode());
        assertEquals(123, response.getData());
    }
}
