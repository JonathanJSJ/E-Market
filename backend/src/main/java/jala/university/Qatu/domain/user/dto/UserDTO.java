package jala.university.Qatu.domain.user.dto;

import jala.university.Qatu.domain.user.enums.UserRole;

public record UserDTO (String firstName, String lastName, Integer age, String email, String password, UserRole role){
}
