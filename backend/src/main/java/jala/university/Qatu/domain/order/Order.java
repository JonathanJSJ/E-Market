package jala.university.Qatu.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jala.university.Qatu.domain.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@Table(name = "orders")
@Entity(name = "order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.WAITING_PAYMENT;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private BigDecimal subtotal;

    private BigDecimal shipping;

    private BigDecimal total;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }


    public void calculateSubTotal() {
        this.subtotal = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void calculateTotal() {
        this.total = subtotal.add(shipping);
    }

}
