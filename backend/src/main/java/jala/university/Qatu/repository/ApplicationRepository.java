package jala.university.Qatu.repository;

import java.util.Optional;
import java.util.UUID;

import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import jala.university.Qatu.domain.user.application.SellerApplication;

public interface ApplicationRepository extends JpaRepository<SellerApplication, UUID>{
    Optional<SellerApplication> findByUser(User user);
    Optional<SellerApplication> findByUserAndApplicationStatus(User user, ApplicationStatus status);

    Optional<SellerApplication> findTopByUserAndApplicationStatusOrderByUpdatedAtDesc(
            User user,
            ApplicationStatus status
    );

    Page<SellerApplication> findAllByApplicationStatus(
            ApplicationStatus status,
            Pageable pageable
    );
}
