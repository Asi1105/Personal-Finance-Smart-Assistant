package com.sydney.uni.backend.utils;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String token;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        token = jwtUtil.generateToken("test@example.com", 1L);
    }

    @Test
    void testGenerateToken_NotNull() {
        assertNotNull(token);
    }

    @Test
    void testExtractUsername() {
        String username = jwtUtil.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void testExtractUserId() {
        Long userId = jwtUtil.extractUserId(token);
        assertEquals(1L, userId);
    }

    @Test
    void testExtractExpiration() {
        Date expiration = jwtUtil.extractExpiration(token);
        assertNotNull(expiration);
    }

    @Test
    void testValidateToken_Valid() {
        boolean valid = jwtUtil.validateToken(token, "test@example.com");
        assertTrue(valid);
    }

    @Test
    void testValidateToken_InvalidEmail() {
        boolean valid = jwtUtil.validateToken(token, "wrong@example.com");
        assertFalse(valid);
    }

    @Test
    void testExpiredToken_ShouldThrowException() throws Exception {
        // Build a short-lived JwtUtil subclass
        JwtUtil shortLivedUtil = new JwtUtil() {
            @Override
            public String generateToken(String email, Long userId) {
                try {
                    Method keyMethod = JwtUtil.class.getDeclaredMethod("getSigningKey");
                    keyMethod.setAccessible(true);
                    SecretKey secretKey = (SecretKey) keyMethod.invoke(this);

                    return io.jsonwebtoken.Jwts.builder()
                            .setSubject(email)
                            .setIssuedAt(new Date(System.currentTimeMillis()))
                            .setExpiration(new Date(System.currentTimeMillis() + 500)) // expires in 0.5s
                            .claim("userId", userId)
                            .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
                            .compact();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        String shortToken = shortLivedUtil.generateToken("expire@test.com", 999L);
        assertNotNull(shortToken);

        Thread.sleep(700); // ensure the token expires

        // Expect ExpiredJwtException from public API
        assertThrows(ExpiredJwtException.class, () -> shortLivedUtil.extractUsername(shortToken));
    }

    @Test
    void testIsTokenExpired_PrivateCheck() throws Exception {
        Method method = JwtUtil.class.getDeclaredMethod("extractExpiration", String.class);
        method.setAccessible(true);
        Date expiration = (Date) method.invoke(jwtUtil, token);
        assertNotNull(expiration);
    }
}
