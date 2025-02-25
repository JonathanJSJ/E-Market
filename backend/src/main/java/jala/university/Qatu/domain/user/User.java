package jala.university.Qatu.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.rating.Rating;
import jala.university.Qatu.domain.user.application.SellerApplication;
import jala.university.Qatu.domain.user.auth.RegisterDTO;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@Table(name = "users")
@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    @Builder.Default
    private Integer age = 18;
    private String email;
    private String password;

    @Builder.Default
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Builder.Default
    private String googleId = "";

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SellerApplication> sellerApplications;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Rating> ratings;

    public User(String firstName, String lastName, String email, Integer age, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.password = password;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
    }

    public User(UUID id, String firstName, String lastName, String email, Integer age, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.password = password;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.getRole().toUpperCase()));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE;
    }

    public static User fromDto(RegisterDTO registerDTO, String encryptedPass) {
        User user = new User();
        user.firstName = registerDTO.firstName();
        user.lastName = registerDTO.lastName();
        user.age = registerDTO.age();
        user.email = registerDTO.email();
        user.password = encryptedPass;
        user.status = UserStatus.ACTIVE;
        user.role = UserRole.USER;
        return user;
    }
}
