package jala.university.Qatu.controller;

import jala.university.Qatu.domain.rating.RatingRequestDTO;
import jala.university.Qatu.domain.rating.RatingResponseDTO;
import jala.university.Qatu.service.ProductService;
import jala.university.Qatu.service.RatingService;
import jala.university.Qatu.service.exceptions.CustomError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private RatingController ratingController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateProductRating() {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO("Great product", (byte) 5, UUID.randomUUID());
        RatingResponseDTO ratingResponseDTO = new RatingResponseDTO("Great product", (byte) 5, "John");
        when(ratingService.createRating(ratingRequestDTO)).thenReturn(ratingResponseDTO);

        ResponseEntity response = ratingController.createProductRating(ratingRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ratingResponseDTO, response.getBody());
    }

    @Test
    public void testCreateProductRatingUnauthorized() {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO("Great product", (byte) 5, UUID.randomUUID());
        when(ratingService.createRating(ratingRequestDTO)).thenThrow(new RuntimeException("You didn't bought it"));

        ResponseEntity response = ratingController.createProductRating(ratingRequestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateProductRatingBadRequest() {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO("Great product", (byte) 5, UUID.randomUUID());
        when(ratingService.createRating(ratingRequestDTO)).thenThrow(new RuntimeException("Some error"));

        ResponseEntity response = ratingController.createProductRating(ratingRequestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(new CustomError("Some error"), response.getBody());
    }

    @Test
    public void testGetProductRatingByCreatorUserId() {
        UUID userId = UUID.randomUUID();
        List<RatingResponseDTO> ratings = Collections.singletonList(new RatingResponseDTO("Great product", (byte) 5, "John"));
        when(productService.getProductRatingByCreatorUserId(userId, 0, 10)).thenReturn(ratings);

        ResponseEntity response = ratingController.getProductRatingByCreatorUserId(userId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ratings, response.getBody());
    }

    @Test
    public void testGetProductRatingByCreatorUserIdNotFound() {
        UUID userId = UUID.randomUUID();
        when(productService.getProductRatingByCreatorUserId(userId, 0, 10)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity response = ratingController.getProductRatingByCreatorUserId(userId, 0, 10);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new CustomError("User not found"), response.getBody());
    }

    @Test
    public void testGetProductMeanRatingByCreatorUserId() {
        UUID userId = UUID.randomUUID();
        Double meanRating = 4.5;
        when(productService.getMeanRatingByCreatorUserId(userId)).thenReturn(meanRating);

        ResponseEntity response = ratingController.getProductMeanRatingByCreatorUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(meanRating, response.getBody());
    }

    @Test
    public void testGetProductMeanRatingByCreatorUserIdNotFound() {
        UUID userId = UUID.randomUUID();
        when(productService.getMeanRatingByCreatorUserId(userId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity response = ratingController.getProductMeanRatingByCreatorUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new CustomError("User not found"), response.getBody());
    }

    @Test
    public void testGetAllRatingsForProduct() {
        UUID productId = UUID.randomUUID();
        List<RatingResponseDTO> ratings = Collections.singletonList(new RatingResponseDTO("Great product", (byte) 5, "John"));
        when(ratingService.getAllRatingsForProduct(productId, 0, 10)).thenReturn(ratings);

        ResponseEntity response = ratingController.getAllRatingsForProduct(productId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ratings, response.getBody());
    }

    @Test
    public void testGetAllRatingsForProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(ratingService.getAllRatingsForProduct(productId, 0, 10)).thenThrow(new RuntimeException("Product not found"));

        ResponseEntity response = ratingController.getAllRatingsForProduct(productId, 0, 10);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new CustomError("Product not found"), response.getBody());
    }

    @Test
    public void testGetMeanRatingForProduct() {
        UUID productId = UUID.randomUUID();
        Double meanRating = 4.5;
        when(ratingService.getMeanRatingForProduct(productId)).thenReturn(meanRating);

        ResponseEntity response = ratingController.getMeanRatingForProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(meanRating, response.getBody());
    }

    @Test
    public void testGetMeanRatingForProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(ratingService.getMeanRatingForProduct(productId)).thenThrow(new RuntimeException("Product not found"));

        ResponseEntity response = ratingController.getMeanRatingForProduct(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new CustomError("Product not found"), response.getBody());
    }

    @Test
    public void testGetUserPossibilityToComment() {
        UUID productId = UUID.randomUUID();
        when(ratingService.userBoughtIt(productId)).thenReturn(true);

        ResponseEntity<Boolean> response = ratingController.getUserPossibilityToComment(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    public void testGetUserPossibilityToCommentUnauthorized() {
        UUID productId = UUID.randomUUID();
        when(ratingService.userBoughtIt(productId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<Boolean> response = ratingController.getUserPossibilityToComment(productId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testGetUserPossibilityToCommentInternalServerError() {
        UUID productId = UUID.randomUUID();
        when(ratingService.userBoughtIt(productId)).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<Boolean> response = ratingController.getUserPossibilityToComment(productId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
