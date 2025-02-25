package jala.university.Qatu.domain.user.dto;

import jala.university.Qatu.domain.user.enums.UserRole;

import java.util.UUID;

public record UserResponseDTO (UUID id, String firstName, String lastName, Integer age, String email, UserRole role){
}
