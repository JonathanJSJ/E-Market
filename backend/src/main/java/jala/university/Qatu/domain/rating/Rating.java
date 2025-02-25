package jala.university.Qatu.domain.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@SuperBuilder
@Table(name = "rating")
@Entity(name = "rating")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String comment;
    @Min(0)
    @Max(5)
    private byte rate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
