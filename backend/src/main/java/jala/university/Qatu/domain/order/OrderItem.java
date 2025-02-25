package jala.university.Qatu.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jala.university.Qatu.domain.product.Product;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal shippingPrice;
}
