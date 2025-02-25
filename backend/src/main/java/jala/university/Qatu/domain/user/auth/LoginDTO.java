package jala.university.Qatu.domain.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @Email(message = "Email should be valid", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email,
        @Size(min = 8, message = "Password must be greater than 8 characters")
        String password) {
}
