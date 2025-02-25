package jala.university.Qatu.domain.user.enums;


import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("admin"),
    USER("user"),
    SELLER("seller");

    private final String role;

    UserRole(String role){
        this.role = role;
    }
}
