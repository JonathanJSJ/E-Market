package jala.university.Qatu.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCartItemDTO {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}
