package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jala.university.Qatu.domain.rating.RatingRequestDTO;
import jala.university.Qatu.domain.rating.RatingResponseDTO;
import jala.university.Qatu.service.ProductService;
import jala.university.Qatu.service.RatingService;
import jala.university.Qatu.service.exceptions.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rating")
@Tag(name = "Rating Controller", description = "Express the user's expression about a seller or product.")
public class RatingController {
    private final RatingService ratingService;
    private final ProductService productService;

    public RatingController(RatingService ratingService, ProductService productService) {
        this.ratingService = ratingService;
        this.productService = productService;
    }

    @Operation(summary = "Create a new rating", description = "Rate a product.\n The rating should be between 0 and 5")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    public ResponseEntity createProductRating(@RequestBody RatingRequestDTO ratingRequestDTO) {
        try {
            return ResponseEntity.ok(ratingService.createRating(ratingRequestDTO));
        } catch (Exception e) {
            if (e.getMessage().equals("You didn't bought it or You already commented on this product")) return ResponseEntity.status(HttpStatus.CONFLICT).build();
            return ResponseEntity.status(400).body(new CustomError(e.getMessage()));
        }
    }

    @Operation(summary = "Get ratings by product creator", description = "Return all ratings for products created by a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/creator/{userId}")
    public ResponseEntity getProductRatingByCreatorUserId(@PathVariable @Parameter(description = "User ID") UUID userId,
                                                          @RequestParam int pageNumber,
                                                          @RequestParam int pageSize) {
        try {
            List<RatingResponseDTO> ratings = productService.getProductRatingByCreatorUserId(userId, pageNumber, pageSize);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomError(e.getMessage()));
        }
    }

    @Operation(summary = "Get mean rating by product creator", description = "Return the mean rating for products created by a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/creator/{userId}/mean")
    public ResponseEntity getProductMeanRatingByCreatorUserId(@PathVariable @Parameter(description = "User ID") UUID userId) {
        try {
            Double meanRating = productService.getMeanRatingByCreatorUserId(userId);
            return ResponseEntity.ok(meanRating);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomError(e.getMessage()));
        }
    }

    @Operation(summary = "Get all ratings for a product", description = "Return all ratings for a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity getAllRatingsForProduct(@PathVariable @Parameter(description = "Product ID") UUID productId,
                                                  @RequestParam int pageNumber,
                                                  @RequestParam int pageSize) {
        try {
            List<RatingResponseDTO> ratings = ratingService.getAllRatingsForProduct(productId, pageNumber, pageSize);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomError(e.getMessage()));
        }
    }

    @Operation(summary = "Get mean rating for a product", description = "Return the mean rating for a specific product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/product/{productId}/mean")
    public ResponseEntity getMeanRatingForProduct(@PathVariable @Parameter(description = "Product ID") UUID productId) {
        try {
            Double meanRating = ratingService.getMeanRatingForProduct(productId);
            return ResponseEntity.ok(meanRating);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new CustomError(e.getMessage()));
        }
    }

    @Operation(summary = "Check if the user bought the product", description = "This endpoint is used to check if the user has bought the product, then it can create a comment and a rating.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "User not logged"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/product/{productId}/confirmation")
    public ResponseEntity<Boolean> getUserPossibilityToComment(@PathVariable @Parameter(description = "Product ID") UUID productId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ratingService.userBoughtIt(productId));
        } catch (Exception e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
