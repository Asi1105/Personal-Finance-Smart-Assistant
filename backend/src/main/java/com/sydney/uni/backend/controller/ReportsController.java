package com.sydney.uni.backend.controller;

import com.sydney.uni.backend.dto.ReportsDto;
import com.sydney.uni.backend.services.ReportsService;
import com.sydney.uni.backend.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {
    
    private final ReportsService reportsService;
    private final JwtUtil jwtUtil;
    
    private static final String BEARER_PREFIX = "Bearer ";
    
    public ReportsController(ReportsService reportsService, JwtUtil jwtUtil) {
        this.reportsService = reportsService;
        this.jwtUtil = jwtUtil;
    }
    
    @GetMapping
    public ResponseEntity<Object> getReports(
            @RequestParam(defaultValue = "6months") String period,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            return ResponseEntity.status(401).body("Unauthorized: Missing or invalid token");
        }

        try {
            String tokenValue = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtUtil.extractUserId(tokenValue);
            
            if (userId != null) {
                // Get reports data
                ReportsDto reportsData = reportsService.getReportsData(userId, period);
                return ResponseEntity.ok(reportsData);
            }
        } catch (Exception e) {
            // Token parsing failed
        }

        return ResponseEntity.status(401).body("Unauthorized: Invalid token");
    }
}
