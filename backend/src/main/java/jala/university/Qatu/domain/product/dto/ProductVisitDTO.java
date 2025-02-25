package jala.university.Qatu.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductVisitDTO {
    private SellerDTO seller;
    private GetProductDTO product;

    @Data
    @AllArgsConstructor
    public static class SellerDTO {
        private UUID id;
        private String fullName;
        private Double averageRating;
        private LocalDateTime acceptedAsSellerDate;
    }
}
