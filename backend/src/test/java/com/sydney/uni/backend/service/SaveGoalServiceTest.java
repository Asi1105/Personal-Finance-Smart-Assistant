package com.sydney.uni.backend.service;

import com.sydney.uni.backend.dto.SaveGoalRequest;
import com.sydney.uni.backend.entity.SaveGoal;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.SaveGoalRepository;
import com.sydney.uni.backend.repository.UserRepository;
import com.sydney.uni.backend.services.SaveGoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaveGoalServiceTest {

    @Mock
    private SaveGoalRepository saveGoalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SaveGoalService saveGoalService;

    private SaveGoalRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new SaveGoalRequest();
        request.setTargetAmount(5000.0);
        request.setDescription("Vacation savings");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
    }

    @Test
    void testSetSaveGoal_CreateNewGoal() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.empty());

        SaveGoal newGoal = new SaveGoal();
        newGoal.setTargetAmount(5000.0);
        newGoal.setDescription("Vacation savings");
        when(saveGoalRepository.save(any(SaveGoal.class))).thenReturn(newGoal);

        SaveGoal result = saveGoalService.setSaveGoal(1L, request);

        assertNotNull(result);
        assertEquals(5000.0, result.getTargetAmount());
        assertEquals("Vacation savings", result.getDescription());
        verify(saveGoalRepository, times(1)).save(any(SaveGoal.class));
    }

    @Test
    void testSetSaveGoal_UpdateExistingGoal() {
        SaveGoal existingGoal = new SaveGoal();
        existingGoal.setTargetAmount(1000.0);
        existingGoal.setDescription("Old goal");
        existingGoal.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.of(existingGoal));
        when(saveGoalRepository.save(any(SaveGoal.class))).thenReturn(existingGoal);

        SaveGoal result = saveGoalService.setSaveGoal(1L, request);

        assertNotNull(result);
        assertEquals(5000.0, result.getTargetAmount());
        assertEquals("Vacation savings", result.getDescription());
    }

    @Test
    void testGetSaveGoal_Found() {
        SaveGoal goal = new SaveGoal();
        goal.setTargetAmount(5000.0);
        goal.setDescription("Test goal");
        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.of(goal));

        SaveGoal result = saveGoalService.getSaveGoal(1L);

        assertNotNull(result);
        assertEquals(5000.0, result.getTargetAmount());
    }

    @Test
    void testGetSaveGoal_NotFound() {
        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.empty());

        SaveGoal result = saveGoalService.getSaveGoal(1L);

        assertNull(result);
    }

    @Test
    void testUpdateSaveGoal_Success() {
        SaveGoal goal = new SaveGoal();
        goal.setTargetAmount(2000.0);
        goal.setDescription("Old goal");

        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.of(goal));
        when(saveGoalRepository.save(any(SaveGoal.class))).thenReturn(goal);

        SaveGoal result = saveGoalService.updateSaveGoal(1L, request);

        assertEquals(5000.0, result.getTargetAmount());
        assertEquals("Vacation savings", result.getDescription());
    }

    @Test
    void testUpdateSaveGoal_NotFound() {
        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                saveGoalService.updateSaveGoal(1L, request));

        assertEquals("Save goal not found", exception.getMessage());
    }

    @Test
    void testDeleteSaveGoal_Success() {
        SaveGoal goal = new SaveGoal();
        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.of(goal));

        saveGoalService.deleteSaveGoal(1L);

        verify(saveGoalRepository, times(1)).delete(goal);
    }

    @Test
    void testDeleteSaveGoal_NotFound() {
        when(saveGoalRepository.findByUserId(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                saveGoalService.deleteSaveGoal(1L));

        assertEquals("Save goal not found", exception.getMessage());
    }
}
