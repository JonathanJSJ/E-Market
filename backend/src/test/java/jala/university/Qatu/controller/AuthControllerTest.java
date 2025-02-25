package jala.university.Qatu.controller;

import jala.university.Qatu.domain.google.GoogleAuthRequest;
import jala.university.Qatu.domain.google.GoogleUserResponse;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.auth.LoginDTO;
import jala.university.Qatu.domain.user.auth.RegisterDTO;
import jala.university.Qatu.domain.user.auth.response.LoginResponse;
import jala.university.Qatu.domain.user.auth.response.RegisterResponse;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;
import jala.university.Qatu.infra.security.TokenService;
import jala.university.Qatu.repository.UserRepository;
import jala.university.Qatu.service.GoogleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private GoogleAuthService googleAuthService;
    @Mock
    private Authentication authentication;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(
                authenticationManager,
                userRepository,
                tokenService,
                passwordEncoder,
                googleAuthService
        );
    }

    @Test
    void login_Success() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("test@test.com", "password123");
        User user = createMockUser();
        String expectedToken = "jwt-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn(expectedToken);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedToken, response.getBody().token());
    }

    @Test
    void login_Failure() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("test@test.com", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void register_Success() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO(
                "John",
                "Doe",
                25,
                "john@test.com",
                "password123"
        );
        when(userRepository.findByEmail(registerDTO.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(createMockUser());

        // Act
        ResponseEntity<?> response = authController.register(registerDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof RegisterResponse);
    }

    @Test
    void register_UserAlreadyExists() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO(
                "John",
                "Doe",
                25,
                "existing@test.com",
                "password123"
        );
        when(userRepository.findByEmail(registerDTO.email()))
                .thenReturn(Optional.of(createMockUser()));

        // Act
        ResponseEntity<?> response = authController.register(registerDTO);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("This email already exists.", response.getBody());
    }

    @Test
    void authenticateGoogleUser_Success() {
        // Arrange
        GoogleAuthRequest authRequest = new GoogleAuthRequest("google-token", "test@gmail.com");
        GoogleUserResponse googleUserResponse = new GoogleUserResponse("test@gmail.com", "password123");
        User user = createMockUser();
        String expectedToken = "jwt-token";

        when(googleAuthService.authenticateGoogleUser(authRequest.token(), authRequest.email()))
                .thenReturn(googleUserResponse);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn(expectedToken);

        // Act
        ResponseEntity<LoginResponse> response = authController.authenticateGoogleUser(authRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedToken, response.getBody().token());
    }

    private User createMockUser() {
        return new User(
                UUID.randomUUID(),
                "John",
                "Doe",
                "test@test.com",
                25,
                "encodedPassword"
        );
    }
}