package jala.university.Qatu.domain.user.auth.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterResponse (
        UUID id,
        String firstName,
        String lastName,
        Integer age,
        @Email(message = "Email should be valid", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email
) {
}
