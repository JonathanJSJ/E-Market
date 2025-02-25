package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.dto.UserDTO;
import jala.university.Qatu.domain.user.dto.UserResponseDTO;
import jala.university.Qatu.domain.user.dto.UserTokenDTO;
import jala.university.Qatu.infra.security.TokenService;
import jala.university.Qatu.service.ConversionService;
import jala.university.Qatu.service.UserDetailsServiceImpl;
import jala.university.Qatu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "Endpoints for managing users. These endpoints are accessible only by administrators.")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserController(UserService service,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService,
                          UserDetailsServiceImpl userDetailsService) {
        this.userService = service;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    @RolesAllowed("ROLE_ADMIN")
    @Operation(
            summary = "Retrieve all users",
            description = "Fetch a paginated list of all registered users. Accessible only to users with the ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied, only admins are allowed")
    })
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve user by ID",
            description = "Fetch a user's details based on their unique identifier (UUID). Accessible to all authenticated users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found with provided ID")
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(ConversionService.fromEntityToOrderItemDTO(user)) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user details",
            description = "Update user information, such as name, age, and role. Accessible only to authorized users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found with provided ID"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "403", description = "User unauthorized")
    })
    public ResponseEntity updateUser(@PathVariable UUID id, @RequestBody UserDTO data) {
        try {
            User updatedUser = userService.updateUser(id, data);
            return ResponseEntity.status(HttpStatus.OK).body(ConversionService.fromEntityToOrderItemDTO(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping
    @Operation(
            summary = "Add a new user",
            description = "This endpoint allows the creation of a new user with basic details like name, age, email, and password. Only available for administrators or authorized personnel."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "409", description = "A user with the same email already exists")
    })
    public ResponseEntity<User> addUser(@Valid @RequestBody UserDTO data) {
        try {
            UserDTO userDTO = new UserDTO(
                    data.firstName(),
                    data.lastName(),
                    data.age(),
                    data.email(),
                    passwordEncoder.encode(data.password()),
                    data.role()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.addUser(userDTO));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("User already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user",
            description = "Remove a user from the system based on their unique identifier (UUID). Accessible only to administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found with provided ID"),
            @ApiResponse(responseCode = "403", description = "Access denied, only admins are allowed")
    })
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        try {
            this.userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/profile")
    @Operation(
            summary = "Retrieve user profile by token",
            description = "Fetch the currently authenticated user's profile based on the JWT token provided in the Authorization header."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access, invalid or missing token")
    })
    public ResponseEntity getUserByToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);

        String userEmail = tokenService.getUserEmailFromToken(token);
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var user = new UserTokenDTO((User) userDetailsService.loadUserByUsername(userEmail));

        return ResponseEntity.ok(user);
    }
}
