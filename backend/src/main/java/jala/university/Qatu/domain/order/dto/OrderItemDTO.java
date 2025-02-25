package jala.university.Qatu.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal shipping;

}
