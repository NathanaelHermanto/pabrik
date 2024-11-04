package com.nathancorp.pabrik;

import com.nathancorp.pabrik.model.Role;
import com.nathancorp.pabrik.model.User;
import com.nathancorp.pabrik.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private User testUser;

    private final String secretKey = "RWl2V2iChIEyicARsgOAnIJ2R/nYqFTqaZS21BZ3Wns=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        testUser = new User(new UUID(1, 0), "TestName", "Lastname", "some@email.com", "password", Role.USER);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token, "Token should be generated");
    }

    @Test
    void testValidateToken() {
        String token = jwtService.generateToken(testUser);
        assertTrue(jwtService.isTokenValid(token, testUser), "Token should be valid");
    }

    @Test
    void testExtractUsernameFromToken() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);
        assertEquals(testUser.getUsername(), username, "Extracted username should match");
    }
}

