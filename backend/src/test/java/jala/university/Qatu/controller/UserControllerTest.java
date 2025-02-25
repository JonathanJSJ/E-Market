package jala.university.Qatu.controller;

import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.dto.UserDTO;
import jala.university.Qatu.domain.user.dto.UserResponseDTO;
import jala.university.Qatu.domain.user.dto.UserTokenDTO;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.infra.security.TokenService;
import jala.university.Qatu.service.UserDetailsServiceImpl;
import jala.university.Qatu.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        Page<UserResponseDTO> users = new PageImpl<>(Collections.emptyList());
        when(userService.getAllUsers(1, 10)).thenReturn(users);

        ResponseEntity<Page<UserResponseDTO>> response = controller.getUsers(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetUserByIdFound() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userService.getUserById(userId)).thenReturn(user);

        ResponseEntity<UserResponseDTO> response = controller.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateUserSuccess() {
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "john.doe@example.com", "password", UserRole.USER);
        User user = new User();
        user.setId(userId);
        when(userService.updateUser(userId, userDTO)).thenReturn(user);

        ResponseEntity response = controller.updateUser(userId, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testUpdateUserNotFound() {
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "john.doe@example.com", "password", UserRole.USER);
        when(userService.updateUser(userId, userDTO)).thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity response = controller.updateUser(userId, userDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateUserUnauthorized() {
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "john.doe@example.com", "password", UserRole.USER);
        when(userService.updateUser(userId, userDTO)).thenThrow(new RuntimeException("User unauthorized"));

        ResponseEntity response = controller.updateUser(userId, userDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testAddUserSuccess() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "john.doe@example.com", "password", UserRole.USER);
        User user = new User();
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(userService.addUser(any(UserDTO.class))).thenReturn(user);

        ResponseEntity<User> response = controller.addUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testAddUserConflict() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "john.doe@example.com", "password", UserRole.USER);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(userService.addUser(any(UserDTO.class))).thenThrow(new IllegalArgumentException("User already exists"));

        ResponseEntity<User> response = controller.addUser(userDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testAddUserBadRequest() {
        UserDTO userDTO = new UserDTO("John", "Doe", 30, "john.doe@example.com", "password", UserRole.USER);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(userService.addUser(any(UserDTO.class))).thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<User> response = controller.addUser(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteUserSuccess() {
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<String> response = controller.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteUserNotFound() {
        UUID userId = UUID.randomUUID();
        doThrow(new IllegalArgumentException()).when(userService).deleteUser(userId);

        ResponseEntity<String> response = controller.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetUserByTokenSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(tokenService.getUserEmailFromToken("token")).thenReturn("john.doe@example.com");
        User user = new User();
        user.setEmail("john.doe@example.com");
        when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(user);

        ResponseEntity response = controller.getUserByToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetUserByTokenUnauthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(tokenService.getUserEmailFromToken("token")).thenReturn(null);

        ResponseEntity response = controller.getUserByToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
