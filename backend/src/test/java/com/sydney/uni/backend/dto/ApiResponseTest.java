package com.sydney.uni.backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void ok_shouldSetSuccessTrueAndData() {
        String payload = "DATA";
        ApiResponse<String> resp = ApiResponse.ok(payload);
        assertTrue(resp.isSuccess());
        assertEquals(payload, resp.getData());
        assertNull(resp.getMessage());
        assertNull(resp.getError());
    }

    @Test
    void fail_shouldSetSuccessFalseAndMessageAndError() {
        ErrorResponse error = new ErrorResponse("msg", "CODE", null);
        ApiResponse<Void> resp = ApiResponse.fail("boom", error);
        assertFalse(resp.isSuccess());
        assertNull(resp.getData());
        assertEquals("boom", resp.getMessage());
        assertEquals(error, resp.getError());
    }

    @Test
    void gettersAndSetters_coverBranches() {
        ApiResponse<Integer> resp = new ApiResponse<>();
        resp.setSuccess(false);
        resp.setData(123);
        resp.setMessage("m");
        ErrorResponse e = new ErrorResponse();
        e.setMessage("em");
        e.setCode("C");
        e.setDetails(null);
        resp.setError(e);

        assertFalse(resp.isSuccess());
        assertEquals(123, resp.getData());
        assertEquals("m", resp.getMessage());
        assertEquals("em", resp.getError().getMessage());
        assertEquals("C", resp.getError().getCode());
        assertNull(resp.getError().getDetails());
    }

    @Test
    void testStaticOkMethod() {
        ApiResponse<String> response = ApiResponse.ok("Success Data");
        assertTrue(response.isSuccess());
        assertEquals("Success Data", response.getData());
        assertNull(response.getMessage());
        assertNull(response.getError());
    }

    @Test
    void testStaticFailMethod() {
        ErrorResponse error = new ErrorResponse("Test Error", "CODE_123", null);
        ApiResponse<Object> response = ApiResponse.fail("Failure Message", error);
        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertEquals("Failure Message", response.getMessage());
        assertEquals(error, response.getError());
    }

    @Test
    void testDefaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        assertNotNull(response);
    }

    @Test
    void testParameterizedConstructor() {
        ErrorResponse error = new ErrorResponse("Test Error", "CODE_123", null);
        ApiResponse<Integer> response = new ApiResponse<>(false, 123, "Test Msg", error);
        assertFalse(response.isSuccess());
        assertEquals(123, response.getData());
        assertEquals("Test Msg", response.getMessage());
        assertEquals(error, response.getError());
    }

    @Test
    void testSettersAndGetters() {
        ApiResponse<String> response = new ApiResponse<>();
        ErrorResponse error = new ErrorResponse();
        response.setSuccess(true);
        response.setData("My Data");
        response.setMessage("My Message");
        response.setError(error);
        assertTrue(response.isSuccess());
        assertEquals("My Data", response.getData());
        assertEquals("My Message", response.getMessage());
        assertEquals(error, response.getError());
    }
}