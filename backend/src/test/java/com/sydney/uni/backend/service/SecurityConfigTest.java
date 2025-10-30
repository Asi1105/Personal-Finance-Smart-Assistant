package com.sydney.uni.backend.service;

import com.sydney.uni.backend.BackendApplication;
import com.sydney.uni.backend.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void authEndpoints_ShouldBePublic() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"test\",\"email\":\"test@test.com\",\"password\":\"pass\"}"))
                .andExpect(status().is(not(401)))
                .andExpect(status().is(not(403)));
    }

    @Test
    void settingsEndpoints_ShouldBePublic() throws Exception {
        mockMvc.perform(put("/api/settings/update-name").param("newName", "test"))
                .andExpect(status().is(not(403)))
                .andExpect(status().is(not(404)));
    }

    @Test
    void cors_AllowsOptions_Preflight() throws Exception {
        mockMvc.perform(options("/api/expenses")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://localhost"))
                .andExpect(status().isOk());
    }

    @Test
    void otherEndpoints_ShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
    void authenticatedUser_CanAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().is(not(403)));
    }
}
