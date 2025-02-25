package jala.university.Qatu.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductDTO {
    private UUID id;
    private String name;
    private String image;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private Double rating;
}
