package jala.university.Qatu.domain.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartInfoDTO {
    private BigDecimal totalPrice;
    private BigDecimal subTotal;
    private BigDecimal shippingCost;
}
