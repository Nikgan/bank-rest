package org.example.bank.security;

import org.example.bank.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "my-super-secret-key-12345678901234567890");
        ReflectionTestUtils.setField(jwtTokenProvider, "validityInMin", 10L);
    }

    @Test
    void createToken_AndValidate_ShouldWork() {
        Role userRole = new Role();
        Role adminRole = new Role();
        userRole.setName("USER");
        adminRole.setName("ADMIN");
        Set<Role> roles = Set.of(userRole, adminRole);

        String token = jwtTokenProvider.createToken("testuser", roles);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));

        String username = jwtTokenProvider.getUsername(token);
        assertEquals("testuser", username);

        List<String> tokenRoles = jwtTokenProvider.getRoles(token);
        assertTrue(tokenRoles.contains("USER"));
        assertTrue(tokenRoles.contains("ADMIN"));
    }

    @Test
    void validateToken_InvalidSignature_ShouldReturnFalse() {
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.invalid.signature";
        assertFalse(jwtTokenProvider.validateToken(fakeToken));
    }
}