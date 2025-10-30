package com.sydney.uni.backend.services;

import com.sydney.uni.backend.dto.SaveGoalRequest;
import com.sydney.uni.backend.entity.SaveGoal;
import com.sydney.uni.backend.entity.User;
import com.sydney.uni.backend.repository.SaveGoalRepository;
import com.sydney.uni.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SaveGoalService {

    private final SaveGoalRepository saveGoalRepository;
    private final UserRepository userRepository;

    public SaveGoalService(SaveGoalRepository saveGoalRepository, UserRepository userRepository) {
        this.saveGoalRepository = saveGoalRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SaveGoal setSaveGoal(Long userId, SaveGoalRequest saveGoalRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already has a save goal
        Optional<SaveGoal> existingSaveGoal = saveGoalRepository.findByUserId(userId);
        
        SaveGoal saveGoal;
        if (existingSaveGoal.isPresent()) {
            // Update existing save goal
            saveGoal = existingSaveGoal.get();
            saveGoal.setTargetAmount(saveGoalRequest.getTargetAmount());
            saveGoal.setDescription(saveGoalRequest.getDescription());
        } else {
            // Create new save goal
            saveGoal = new SaveGoal();
            saveGoal.setUser(user);
            saveGoal.setTargetAmount(saveGoalRequest.getTargetAmount());
            saveGoal.setDescription(saveGoalRequest.getDescription());
        }

        return saveGoalRepository.save(saveGoal);
    }

    public SaveGoal getSaveGoal(Long userId) {
        Optional<SaveGoal> saveGoal = saveGoalRepository.findByUserId(userId);
        return saveGoal.orElse(null);
    }

    @Transactional
    public SaveGoal updateSaveGoal(Long userId, SaveGoalRequest saveGoalRequest) {
        SaveGoal saveGoal = saveGoalRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Save goal not found"));

        saveGoal.setTargetAmount(saveGoalRequest.getTargetAmount());
        saveGoal.setDescription(saveGoalRequest.getDescription());

        return saveGoalRepository.save(saveGoal);
    }

    @Transactional
    public void deleteSaveGoal(Long userId) {
        SaveGoal saveGoal = saveGoalRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Save goal not found"));

        saveGoalRepository.delete(saveGoal);
    }
}
