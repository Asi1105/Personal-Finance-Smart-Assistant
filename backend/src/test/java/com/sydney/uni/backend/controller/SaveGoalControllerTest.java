package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.SaveGoalRequest;
import com.sydney.uni.backend.entity.SaveGoal;
import com.sydney.uni.backend.services.SaveGoalService;
import com.sydney.uni.backend.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SaveGoalControllerTest {

    @Mock
    private SaveGoalService saveGoalService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private SaveGoalController saveGoalController;

    private static final String VALID_TOKEN = "Bearer valid.jwt.token";
    private static final String VALID_TOKEN_VALUE = "valid.jwt.token";
    private static final String INVALID_TOKEN = "Bearer invalid.token";
    private static final String INVALID_TOKEN_VALUE = "invalid.token";
    private static final Long USER_ID = 1L;

    private SaveGoalRequest saveGoalRequest;
    private SaveGoal mockSaveGoal;

    @BeforeEach
    void setup() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(USER_ID);

        saveGoalRequest = new SaveGoalRequest();
        saveGoalRequest.setTargetAmount(5000.0);
        saveGoalRequest.setDescription("Trip to Japan");

        mockSaveGoal = new SaveGoal();
        mockSaveGoal.setId(1L);
        // The problematic line 'mockSaveGoal.setUserId(USER_ID);' has been removed.
        mockSaveGoal.setTargetAmount(5000.0);
        mockSaveGoal.setDescription("Trip to Japan");
    }

    // --- setSaveGoal (POST) Tests ---

    @Test
    void testSetSaveGoal_Success() {
        when(saveGoalService.setSaveGoal(eq(USER_ID), any(SaveGoalRequest.class)))
                .thenReturn(mockSaveGoal);

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.setSaveGoal(VALID_TOKEN, saveGoalRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(5000.0, response.getBody().getData().getTargetAmount());
        assertEquals("Trip to Japan", response.getBody().getData().getDescription());
    }

    @Test
    void testSetSaveGoal_Unauthorized() {
        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.setSaveGoal(null, saveGoalRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testSetSaveGoal_HeaderWithoutBearer_Unauthorized() {
        String badHeader = "Token sometoken"; // not starting with Bearer
        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.setSaveGoal(badHeader, saveGoalRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testSetSaveGoal_ServiceFailure() {
        when(saveGoalService.setSaveGoal(eq(USER_ID), any(SaveGoalRequest.class)))
                .thenThrow(new RuntimeException("User already has a goal"));

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.setSaveGoal(VALID_TOKEN, saveGoalRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("SAVE_GOAL_FAILED", response.getBody().getError().getCode());
        assertEquals("User already has a goal", response.getBody().getError().getMessage());
    }

    @Test
    void testSetSaveGoal_InvalidToken() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.setSaveGoal(INVALID_TOKEN, saveGoalRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("SAVE_GOAL_FAILED", response.getBody().getError().getCode());
        assertEquals("Invalid signature", response.getBody().getError().getMessage());
    }

    @Test
    void testSetSaveGoal_UserIdNull() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.setSaveGoal(VALID_TOKEN, saveGoalRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    // --- getSaveGoal (GET) Tests ---

    @Test
    void testGetSaveGoal_Success_Found() {
        when(saveGoalService.getSaveGoal(USER_ID)).thenReturn(mockSaveGoal);

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.getSaveGoal(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockSaveGoal, response.getBody().getData());
    }

    @Test
    void testGetSaveGoal_Success_NotFound() {
        when(saveGoalService.getSaveGoal(USER_ID)).thenReturn(null);

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.getSaveGoal(VALID_TOKEN);

        assertEquals(404, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("NO_SAVE_GOAL", response.getBody().getError().getCode());
    }

    @Test
    void testGetSaveGoal_Unauthorized() {
        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.getSaveGoal(null);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testGetSaveGoal_HeaderWithoutBearer_Unauthorized() {
        String badHeader = "Token x";
        ResponseEntity<ApiResponse<SaveGoal>> response = saveGoalController.getSaveGoal(badHeader);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testGetSaveGoal_InvalidToken() {
        when(jwtUtil.extractUserId(INVALID_TOKEN_VALUE)).thenThrow(new JwtException("Invalid signature"));

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.getSaveGoal(INVALID_TOKEN);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("GET_SAVE_GOAL_FAILED", response.getBody().getError().getCode());
        assertTrue(response.getBody().getError().getMessage().contains("Invalid signature"));
    }

    @Test
    void testGetSaveGoal_UserIdNull_Unauthorized() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.getSaveGoal(VALID_TOKEN);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    // --- updateSaveGoal (PUT) Tests ---

    @Test
    void testUpdateSaveGoal_Success() {
        SaveGoal updatedGoal = new SaveGoal();
        updatedGoal.setId(1L);
        updatedGoal.setTargetAmount(6000.0);
        updatedGoal.setDescription("Updated Goal");

        when(saveGoalService.updateSaveGoal(eq(USER_ID), any(SaveGoalRequest.class)))
                .thenReturn(updatedGoal);

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.updateSaveGoal(VALID_TOKEN, saveGoalRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals(6000.0, response.getBody().getData().getTargetAmount());
    }

    @Test
    void testUpdateSaveGoal_HeaderWithoutBearer_Unauthorized() {
        String badHeader = "Token y";
        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.updateSaveGoal(badHeader, saveGoalRequest);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testUpdateSaveGoal_ServiceFailure() {
        when(saveGoalService.updateSaveGoal(eq(USER_ID), any(SaveGoalRequest.class)))
                .thenThrow(new RuntimeException("No goal to update"));

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.updateSaveGoal(VALID_TOKEN, saveGoalRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UPDATE_SAVE_GOAL_FAILED", response.getBody().getError().getCode());
        assertEquals("No goal to update", response.getBody().getError().getMessage());
    }

    @Test
    void testUpdateSaveGoal_UserIdNull_Unauthorized() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<ApiResponse<SaveGoal>> response =
                saveGoalController.updateSaveGoal(VALID_TOKEN, saveGoalRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }

    // --- deleteSaveGoal (DELETE) Tests ---

    @Test
    void testDeleteSaveGoal_Success() {
        doNothing().when(saveGoalService).deleteSaveGoal(USER_ID);

        ResponseEntity<ApiResponse<String>> response =
                saveGoalController.deleteSaveGoal(VALID_TOKEN);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Save goal deleted successfully", response.getBody().getData());
    }

    @Test
    void testDeleteSaveGoal_HeaderWithoutBearer_Unauthorized() {
        String badHeader = "Token z";
        ResponseEntity<ApiResponse<String>> response = saveGoalController.deleteSaveGoal(badHeader);
        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("UNAUTHORIZED", response.getBody().getError().getCode());
    }

    @Test
    void testDeleteSaveGoal_ServiceFailure() {
        doThrow(new RuntimeException("Goal not found")).when(saveGoalService).deleteSaveGoal(USER_ID);

        ResponseEntity<ApiResponse<String>> response =
                saveGoalController.deleteSaveGoal(VALID_TOKEN);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("DELETE_SAVE_GOAL_FAILED", response.getBody().getError().getCode());
        assertEquals("Goal not found", response.getBody().getError().getMessage());
    }

    @Test
    void testDeleteSaveGoal_UserIdNull_Unauthorized() {
        when(jwtUtil.extractUserId(VALID_TOKEN_VALUE)).thenReturn(null);

        ResponseEntity<ApiResponse<String>> response =
                saveGoalController.deleteSaveGoal(VALID_TOKEN);

        assertEquals(401, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("INVALID_TOKEN", response.getBody().getError().getCode());
    }
}