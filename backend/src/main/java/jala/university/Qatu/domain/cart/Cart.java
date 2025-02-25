package jala.university.Qatu.domain.cart;

import jakarta.persistence.*;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.user.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private BigDecimal shipping;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }

    public void updateItemQuantity(Product product, int quantity) {
        items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(quantity),
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .product(product)
                                    .quantity(quantity)
                                    .build();
                            addItem(newItem);
                        }
                );
    }
}
