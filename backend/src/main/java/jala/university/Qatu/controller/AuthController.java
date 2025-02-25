package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jala.university.Qatu.domain.google.GoogleAuthRequest;
import jala.university.Qatu.domain.google.GoogleUserResponse;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.auth.LoginDTO;
import jala.university.Qatu.domain.user.auth.RegisterDTO;
import jala.university.Qatu.domain.user.auth.response.LoginResponse;
import jala.university.Qatu.domain.user.auth.response.RegisterResponse;
import jala.university.Qatu.infra.security.TokenService;
import jala.university.Qatu.repository.UserRepository;
import jala.university.Qatu.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Endpoints for managing authorization.")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthService googleAuthService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository repository, TokenService tokenService, PasswordEncoder passwordEncoder, GoogleAuthService googleAuthService1) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.googleAuthService = googleAuthService1;
    }

    @Operation(summary = "Login a user", description = "Authenticate a user and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid login, wrong credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginDTO data) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            var token = tokenService.generateToken((User) authentication.getPrincipal());
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Register a new user", description = "Create a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Bad Request - User already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (repository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This email already exists.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        User user = User.fromDto(data, encryptedPassword);
        repository.save(user);

        RegisterResponse res = new RegisterResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getAge(), user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @Operation(summary = "Social login with Google", description = "Authenticate a user using Google OAuth2 login and return the email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in via Google successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login-social/google")
    public ResponseEntity<LoginResponse> authenticateGoogleUser(
            @RequestBody GoogleAuthRequest authRequest
    ) {
        GoogleUserResponse user = googleAuthService.authenticateGoogleUser(authRequest.token(), authRequest.email());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.email(), user.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        var token = tokenService.generateToken((User) authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(token));
    }
}
