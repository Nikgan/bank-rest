package org.example.bankcards.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankcards.dto.AuthRequest;
import org.example.bankcards.entity.User;
import org.example.bankcards.repository.CardRepository;
import org.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    void setup() {
        cardRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("email@test.com");
        user.setEnabled(true);
        user.setPasswordHash(passwordEncoder.encode("pass"));
        userRepository.save(user);
    }


    @Test
    @Order(1)
    @DisplayName("POST /auth/login — успешная авторизация")
    void testLoginSuccess() throws Exception {
        AuthRequest req = new AuthRequest("testuser", "pass");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    @Order(2)
    @DisplayName("POST /auth/login — неверный пароль")
    void testLoginWrongPassword() throws Exception {
        AuthRequest req = new AuthRequest("testuser", "wrong");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Неверный логин или пароль")));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/cards без токена — ожидается 403 Forbidden")
    void testGetCardsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isForbidden());
    }
}
