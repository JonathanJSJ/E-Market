package jala.university.Qatu.domain.order.dto;

import jala.university.Qatu.domain.order.OrderStatus;
import jala.university.Qatu.domain.user.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponseDTO {
    private UUID id;
    private UserDTO user;
    private List<OrderItemDTO> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal total;
}