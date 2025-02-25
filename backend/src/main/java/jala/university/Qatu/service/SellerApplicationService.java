package jala.university.Qatu.service;

import jakarta.transaction.Transactional;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.application.SellerApplication;
import jala.university.Qatu.domain.user.enums.ApplicationStatus;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.repository.ApplicationRepository;
import jala.university.Qatu.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SellerApplicationService {
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public SellerApplicationService(
            UserRepository userRepository,
            ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    public void approveOrRejectApplication(UUID applicationId, boolean approve) {
        SellerApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setApplicationStatus(approve ? ApplicationStatus.ACCEPTED : ApplicationStatus.DENIED);

        if (approve) {
            User user = userRepository.findById(application.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Invalid user for seller application"));
            user.setRole(UserRole.SELLER);
            userRepository.save(user);
        }

        applicationRepository.save(application);
    }

    public SellerApplication applyToBeASeller() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new RuntimeException("User not authenticated");
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<SellerApplication> pendingApplication = applicationRepository
                .findByUserAndApplicationStatus(user, ApplicationStatus.PENDING);

        if (pendingApplication.isPresent()) {
            throw new RuntimeException("You already have a pending application");
        }

        Optional<SellerApplication> lastRejectedApplication = applicationRepository
                .findTopByUserAndApplicationStatusOrderByUpdatedAtDesc(user, ApplicationStatus.DENIED);

        if (lastRejectedApplication.isPresent()) {
            LocalDateTime rejectionDate = lastRejectedApplication.get().getUpdatedAt();
            LocalDateTime waitUntil = rejectionDate.plusDays(7);

            if (LocalDateTime.now().isBefore(waitUntil)) {
                long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), waitUntil);
                throw new RuntimeException(String.format(
                        "You must wait %d more days before submitting a new application", daysRemaining));
            }
        }

        SellerApplication application = new SellerApplication();
        application.setUser(user);
        application.setApplicationStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public Page<SellerApplication> getAllApplications(Integer pageNumber, Integer pageSize) {
        return applicationRepository.findAll(PageRequest.of(pageNumber - 1, pageSize));
    }

    public Page<SellerApplication> getApplicationsByStatus(ApplicationStatus status, Integer pageNumber, Integer pageSize) {
        return applicationRepository.findAllByApplicationStatus(
                status,
                PageRequest.of(pageNumber - 1, pageSize)
        );
    }
}
