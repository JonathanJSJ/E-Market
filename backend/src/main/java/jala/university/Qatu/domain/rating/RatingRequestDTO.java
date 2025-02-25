package jala.university.Qatu.domain.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RatingRequestDTO(String comment, @Min(0) @Max(5) byte rate, @NotNull UUID productId) {
}
