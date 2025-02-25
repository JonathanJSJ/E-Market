package jala.university.Qatu.domain.cart.dto;

import jala.university.Qatu.domain.cart.Cart;
import jala.university.Qatu.domain.cart.CartItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class CartResponseDTO {
    private UUID id;
    private UUID userId;
    private List<CartItemDTO> items;
    private BigDecimal total;
    private LocalDateTime updatedAt;

    public static CartResponseDTO fromEntity(Cart cart) {
        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(cart.getItems().stream()
                        .map(CartItemDTO::fromEntity)
                        .collect(Collectors.toList()))
                .total(calculateTotal(cart.getItems()))
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private static BigDecimal calculateTotal(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
