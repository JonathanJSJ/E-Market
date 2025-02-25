package jala.university.Qatu.repository;

import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndGoogleId(String email, String googleId);
    Page<User> findAllByRoleAndStatus(
            UserRole role,
            UserStatus status,
            Pageable pageable
    );
}
