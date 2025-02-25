package jala.university.Qatu.domain.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jala.university.Qatu.domain.rating.Rating;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import jala.university.Qatu.domain.product.enums.ProductStatus;
import jala.university.Qatu.domain.user.User;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@Table(name = "products")
@Entity(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    private BigDecimal price;

    @Builder.Default
    private Integer availableQuantity = 0;

    @Builder.Default
    private ProductStatus productStatus = ProductStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "statistic_id")
    private Statistic statistic;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Rating> ratings;
}
