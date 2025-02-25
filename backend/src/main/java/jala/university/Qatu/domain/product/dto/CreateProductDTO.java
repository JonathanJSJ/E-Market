package jala.university.Qatu.domain.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class CreateProductDTO {
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private Integer stock;
    private String category;
}
