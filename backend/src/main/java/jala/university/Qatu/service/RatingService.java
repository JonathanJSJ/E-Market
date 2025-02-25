package jala.university.Qatu.service;

import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.rating.Rating;
import jala.university.Qatu.domain.rating.RatingRequestDTO;
import jala.university.Qatu.domain.rating.RatingResponseDTO;
import jala.university.Qatu.repository.OrderRepository;
import jala.university.Qatu.repository.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    public RatingService(RatingRepository ratingRepository, UserService userService, ProductService productService, OrderRepository orderRepository) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    public RatingResponseDTO getRating(UUID id) {
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new RuntimeException("Rating not found"));
        return toRatingResponse(rating);
    }

    public RatingResponseDTO createRating(RatingRequestDTO ratingRequestDTO) {

        if (!userBoughtIt(ratingRequestDTO.productId())) throw new RuntimeException("You didn't bought it or You already commented on this product");

        Rating rating = toRating(ratingRequestDTO);

        UUID userId = userService.getCurrentUserId();
        rating.setUser(userService.getUserById(userId));

        rating = ratingRepository.save(rating);

        return toRatingResponse(rating);
    }

    public Rating toRating(RatingRequestDTO ratingRequestDTO) {
        Rating rating = new Rating();
        rating.setComment(ratingRequestDTO.comment());
        rating.setRate(ratingRequestDTO.rate());
        Product product = productService.getProductById(ratingRequestDTO.productId());
        rating.setProduct(product);
        return rating;
    }

    public RatingResponseDTO toRatingResponse(Rating rating) {
        return new RatingResponseDTO(
                rating.getComment(),
                rating.getRate(),
                rating.getUser().getFirstName()
        );
    }

    public List<RatingResponseDTO> getAllRatingsForProduct(UUID productId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Rating> ratingsPage = ratingRepository.findByProductId(productId, pageable);
        return ratingsPage.stream()
                .map(this::toRatingResponse)
                .collect(Collectors.toList());
    }

    public Double getMeanRatingForProduct(UUID productId) {
        List<Rating> ratings = ratingRepository.findAllByProductId(productId);
        return ratings.stream()
                .mapToDouble(Rating::getRate)
                .average()
                .orElse(0.0);
    }

    public Boolean userBoughtIt(UUID productId) {
        return orderRepository.existsByUserEmailAndProductId(userService.getCurrentUserId(), productId) && !ratingRepository.alreadyCommentedByUser(userService.getCurrentUserId(), productId);
    }
}
