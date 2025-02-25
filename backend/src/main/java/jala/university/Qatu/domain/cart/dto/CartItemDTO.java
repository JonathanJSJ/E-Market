package jala.university.Qatu.domain.cart.dto;

import jala.university.Qatu.domain.cart.CartItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CartItemDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private String productImage;
    private BigDecimal subtotal;
    private BigDecimal shippingPrice;

    public static CartItemDTO fromEntity(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .productImage(item.getProduct().getImage())
                .shippingPrice(item.getShipping())
                .subtotal(item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
