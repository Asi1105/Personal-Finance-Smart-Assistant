package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ApiResponse;
import com.sydney.uni.backend.dto.ErrorResponse;
import com.sydney.uni.backend.dto.SaveGoalRequest;
import com.sydney.uni.backend.entity.SaveGoal;
import com.sydney.uni.backend.services.SaveGoalService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/save-goals")
public class SaveGoalController {

    private static final String INVALID_TOKEN_MESSAGE = "Invalid token";
    private static final String UNAUTHORIZED_MESSAGE = "Authorization token required";
    private static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";
    private static final String INVALID_TOKEN_CODE = "INVALID_TOKEN";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String UNAUTHORIZED_TITLE = "Unauthorized";

    private final SaveGoalService saveGoalService;
    private final JwtUtil jwtUtil;

    public SaveGoalController(SaveGoalService saveGoalService, JwtUtil jwtUtil) {
        this.saveGoalService = saveGoalService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SaveGoal>> setSaveGoal(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody SaveGoalRequest saveGoalRequest) {

        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length());
            Long userId = jwtUtil.extractUserId(tokenValue);

            if (userId != null) {
                SaveGoal saveGoal = saveGoalService.setSaveGoal(userId, saveGoalRequest);
                return ResponseEntity.ok(ApiResponse.ok(saveGoal));
            }
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage(), "SAVE_GOAL_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Set Save Goal Failed", error), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Token parsing failed or other unexpected error
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SaveGoal>> getSaveGoal(
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length());
            Long userId = jwtUtil.extractUserId(tokenValue);

            if (userId != null) {
                SaveGoal saveGoal = saveGoalService.getSaveGoal(userId);
                if (saveGoal != null) {
                    return ResponseEntity.ok(ApiResponse.ok(saveGoal));
                } else {
                    ErrorResponse error = new ErrorResponse("No save goal found", "NO_SAVE_GOAL", null);
                    return new ResponseEntity<>(ApiResponse.fail("No Save Goal Found", error), HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Failed to get save goal: " + e.getMessage(), "GET_SAVE_GOAL_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Get Save Goal Failed", error), HttpStatus.BAD_REQUEST);
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<SaveGoal>> updateSaveGoal(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody SaveGoalRequest saveGoalRequest) {

        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length());
            Long userId = jwtUtil.extractUserId(tokenValue);

            if (userId != null) {
                SaveGoal saveGoal = saveGoalService.updateSaveGoal(userId, saveGoalRequest);
                return ResponseEntity.ok(ApiResponse.ok(saveGoal));
            }
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage(), "UPDATE_SAVE_GOAL_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Update Save Goal Failed", error), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Token parsing failed or other unexpected error
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteSaveGoal(
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            ErrorResponse error = new ErrorResponse(UNAUTHORIZED_MESSAGE, UNAUTHORIZED_CODE, null);
            return new ResponseEntity<>(ApiResponse.fail(UNAUTHORIZED_TITLE, error), HttpStatus.UNAUTHORIZED);
        }

        try {
            String tokenValue = token.substring(BEARER_PREFIX.length());
            Long userId = jwtUtil.extractUserId(tokenValue);

            if (userId != null) {
                saveGoalService.deleteSaveGoal(userId);
                return ResponseEntity.ok(ApiResponse.ok("Save goal deleted successfully"));
            }
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage(), "DELETE_SAVE_GOAL_FAILED", null);
            return new ResponseEntity<>(ApiResponse.fail("Delete Save Goal Failed", error), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Token parsing failed or other unexpected error
        }

        ErrorResponse error = new ErrorResponse(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, null);
        return new ResponseEntity<>(ApiResponse.fail(INVALID_TOKEN_MESSAGE, error), HttpStatus.UNAUTHORIZED);
    }
}
