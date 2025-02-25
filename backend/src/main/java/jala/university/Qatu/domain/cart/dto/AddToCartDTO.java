package jala.university.Qatu.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddToCartDTO {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
