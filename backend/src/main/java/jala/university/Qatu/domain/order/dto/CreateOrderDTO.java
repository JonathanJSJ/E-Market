package jala.university.Qatu.domain.order.dto;

import jala.university.Qatu.domain.cart.CartItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateOrderDTO {
    private List<CartItem> items;
}
