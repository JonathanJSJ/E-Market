package jala.university.Qatu.domain.user.application;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class SellerApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
