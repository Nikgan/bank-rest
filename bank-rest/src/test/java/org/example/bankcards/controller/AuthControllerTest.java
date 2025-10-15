package org.example.bankcards.controller;

import org.example.bankcards.dto.AuthRequest;
import org.example.bankcards.dto.JwtResponse;
import org.example.bankcards.dto.RegisterRequest;
import org.example.bankcards.entity.Role;
import org.example.bankcards.entity.User;
import org.example.bankcards.repository.RoleRepository;
import org.example.bankcards.repository.UserRepository;
import org.example.bankcards.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Тест регистрации")
    void register_NewUser_ShouldReturnOk() {
        RegisterRequest req = new RegisterRequest("testuser", "pass", "email@test.com");

        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.empty());
        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded");

        ResponseEntity<?> response = authController.register(req);

        assertEquals(200, response.getStatusCodeValue());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Регистрация уже существ-щего пользователя")
    void register_ExistingUser_ShouldReturnBadRequest() {
        RegisterRequest req = new RegisterRequest("testuser", "pass", "email@test.com");
        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = authController.register(req);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("уже существует"));
    }

    @Test
    @DisplayName("Тест успешного логина")
    void login_ValidCredentials_ShouldReturnToken() {
        AuthRequest req = new AuthRequest("testuser", "pass");
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("encoded");
        Role userRole = new Role();
        userRole.setName("USER");

        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtTokenProvider.createToken(eq("testuser"), anySet())).thenReturn("mocked_token");

        ResponseEntity<?> response = authController.login(req);

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(JwtResponse.class, response.getBody());
        assertEquals("mocked_token", ((JwtResponse) response.getBody()).getToken());
    }

    @Test
    @DisplayName("Проверка авторизации с неверным паролем")
    void login_InvalidPassword_ShouldReturnUnauthorized() {
        AuthRequest req = new AuthRequest("testuser", "wrongpass");
        User user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("encoded");

        when(userRepository.findByUsername(req.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(req.getPassword(), user.getPasswordHash())).thenReturn(false);

        ResponseEntity<?> response = authController.login(req);

        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Неверный логин"));
    }

    @Test
    @DisplayName("Проверка на авторизацию несуществ-го пользователя")
    void login_NonexistentUser_ShouldThrowException() {
        AuthRequest req = new AuthRequest("ghost", "pass");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authController.login(req));
    }
}
