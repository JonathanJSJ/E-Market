package jala.university.Qatu.service;

import jala.university.Qatu.domain.google.GoogleResponse;
import jala.university.Qatu.domain.google.GoogleUserResponse;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;
import jala.university.Qatu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class GoogleAuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public GoogleAuthService(RestTemplate restTemplate, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public GoogleUserResponse authenticateGoogleUser(String accessToken, String email) {
        ResponseEntity<GoogleResponse> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/tokeninfo?id_token=" + accessToken,
                HttpMethod.GET,
                null,
                GoogleResponse.class
        );

        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token.");
        }

        GoogleResponse googleUser = response.getBody();

        assert googleUser != null;

        if (!email.equals(googleUser.email())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This email is not valid for this token.");
        }

        Optional<User> userOptional = userRepository.findByEmailAndGoogleId(email, response.getBody().kid());

        String pass = googleUser.kid().split("")[0] + googleUser.name().split("")[2] + googleUser.kid();

        userOptional.orElseGet(() -> {
            User user = new User();
            user.setGoogleId(googleUser.kid());
            user.setStatus(UserStatus.ACTIVE);
            user.setRole(UserRole.USER);
            user.setPassword(passwordEncoder.encode(pass));

            if (googleUser.name().split(" ").length > 1) {
                user.setFirstName(googleUser.name().split(" ")[0]);
                user.setLastName(googleUser.name().split(" ")[1]);
            } else {
                user.setFirstName(googleUser.name());
                user.setLastName("");
            }
            user.setEmail(googleUser.email());

            return userRepository.save(user);
        });
        return new GoogleUserResponse(googleUser.email(), pass);
    }
}
