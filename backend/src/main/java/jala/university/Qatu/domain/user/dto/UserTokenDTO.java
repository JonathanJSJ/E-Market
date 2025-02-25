package jala.university.Qatu.domain.user.dto;

import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;

import java.util.UUID;

public record UserTokenDTO(
        UUID id,
        String firstName,
        String lastName,
        int age,
        String email,
        UserRole role,
        UserStatus status
) {
    public UserTokenDTO(User user) {
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getAge(), user.getEmail(), user.getRole(), user.getStatus());
    }
}
