package jala.university.Qatu.service;

import jala.university.Qatu.domain.product.Category;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.product.Statistic;
import jala.university.Qatu.domain.product.dto.CreateProductDTO;
import jala.university.Qatu.domain.product.dto.GetProductDTO;
import jala.university.Qatu.domain.product.dto.ProductVisitDTO;
import jala.university.Qatu.domain.rating.Rating;
import jala.university.Qatu.domain.rating.RatingResponseDTO;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.application.SellerApplication;
import jala.university.Qatu.domain.user.enums.ApplicationStatus;
import jala.university.Qatu.repository.*;
import jala.university.Qatu.specification.ProductSpecification;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StatisticRepository statisticRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final RatingService ratingService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, UserService userService, StatisticRepository statisticRepository, RatingRepository ratingRepository, UserRepository userRepository, ApplicationRepository applicationRepository, @Lazy RatingService ratingService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.statisticRepository = statisticRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.ratingService = ratingService;
    }

    public Page<Product> getAllProductsByUserId(UUID id, Pageable pageable) {
        return productRepository.findByUserId(id, pageable);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Map<String, List<GetProductDTO>> getProductRecommendation(int pageSize, String category) {
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "statistic.visits"));

        List<Product> filteredProducts = productRepository.findByCategoryName(category, pageable);

        return filteredProducts.stream()
                .map(this::toDTO)
                .collect(Collectors.groupingBy(GetProductDTO::getCategory));
    }

    public List<GetProductDTO> getProductRecommendation(int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);

        List<Product> filteredProducts = productRepository.findTopProductsByVisitCount(pageable);

        return filteredProducts.stream().map(this::toDTO).toList();
    }


    public Double getMeanRatingByCreatorUserId(UUID userId) {
    List<RatingResponseDTO> ratings = getProductRatingByCreatorUserId(userId, 0, Integer.MAX_VALUE);
    return ratings.stream()
                  .mapToDouble(RatingResponseDTO::rate)
                  .average()
                  .orElse(0.0);
}

    public List<RatingResponseDTO> getProductRatingByCreatorUserId(UUID userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Rating> ratings = ratingRepository.findAllByProductCreatorUserId(userId);

        int start = Math.min((int) pageable.getOffset(), ratings.size());
        int end = Math.min((start + pageable.getPageSize()), ratings.size());
        List<Rating> paginatedRatings = ratings.subList(start, end);

        return paginatedRatings.stream()
                .map(this::toRatingResponse)
                .collect(Collectors.toList());
    }

    public Product createProduct(CreateProductDTO dto) throws IOException {
        UUID userId = userService.getCurrentUserId();
        User user = userService.getUserById(userId);

        Category category = categoryRepository.findByName(dto.getCategory());
        if (category == null) {
            throw new IllegalArgumentException("Invalid category.");
        }

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setAvailableQuantity(dto.getStock());
        product.setUser(user);
        product.setCategory(category);
        product.setImage(dto.getImage());

        Statistic statistic = new Statistic();
        statisticRepository.save(statistic);

        product.setStatistic(statistic);

        return productRepository.save(product);
    }

    public void updateProduct(UUID id, CreateProductDTO dto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!isProductOwner(id, userService.getCurrentUserId())) {
            throw new RuntimeException("You are not the owner of this product");
        }

        existingProduct.setName(dto.getName());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setCategory(categoryRepository.findByName(dto.getCategory()));
        existingProduct.setAvailableQuantity(dto.getStock());

        productRepository.save(existingProduct);
    }

    public void deleteProduct(UUID id) {
        if (!isProductOwner(id, userService.getCurrentUserId())) {
            throw new SecurityException("You are not the owner of this product");
        } else if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public Page<GetProductDTO> getAllProducts(Pageable pageable, Specification<Product> spec) {
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<GetProductDTO> productDTOs = productPage.getContent()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productPage.getTotalElements());
    }

    public ProductVisitDTO addVisit(UUID id) {
        Product product = this.getProductById(id);

        if (product == null) {
            return null;
        }

        Statistic productStatistic = statisticRepository.findById(product.getStatistic().getId())
                .orElseThrow(() -> new RuntimeException("This product statistics doesn't exist"));
        productStatistic.addVisit();
        statisticRepository.save(productStatistic);

        User seller = product.getUser();
        Double averageRating = getMeanRatingByCreatorUserId(seller.getId());
        LocalDateTime acceptedAsSellerDate = getAcceptedAsSellerDate(seller.getId());

        ProductVisitDTO.SellerDTO sellerDTO = new ProductVisitDTO.SellerDTO(
                id,
                seller.getFirstName() + " " + seller.getLastName(),
                averageRating,
                acceptedAsSellerDate
        );

        GetProductDTO productDTO = toDTO(product);

        return new ProductVisitDTO(sellerDTO, productDTO);
    }

    private LocalDateTime getAcceptedAsSellerDate(UUID userId) {
        SellerApplication acceptedApplication = applicationRepository.findByUserAndApplicationStatus(
                userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")),
                ApplicationStatus.ACCEPTED
        ).orElseThrow(() -> new RuntimeException("Seller application was not found"));

        return acceptedApplication.getUpdatedAt();
    }

    public GetProductDTO toDTO(Product product) {
        Double averageRating = ratingService.getMeanRatingForProduct(product.getId());
        return new GetProductDTO(
                product.getId(),
                product.getName(),
                product.getImage(),
                product.getDescription(),
                product.getPrice(),
                product.getAvailableQuantity(),
                product.getCategory().getName(),
                averageRating
        );
    }


    public boolean productExistsByNameAndUserId(String name, UUID userId) {
        return productRepository.existsByNameAndUserId(name, userId);
    }

    public Specification<Product> createSpecification(String name, String category, Double maxPrice, Double minPrice, Double maxRate, Double minRate) {
        return Specification
                .where(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasCategory(category))
                .and(ProductSpecification.hasMaxPrice(maxPrice))
                .and(ProductSpecification.hasMinPrice(minPrice))
                .and(ProductSpecification.hasMinRate(minRate))
                .and(ProductSpecification.hasMaxRate(maxRate));
    }

    public boolean isProductOwner(UUID productId, UUID userId) {
        Product product = getProductById(productId);
        return product.getUser().getId().equals(userId);
    }

    public RatingResponseDTO toRatingResponse(Rating rating) {
        return new RatingResponseDTO(
                rating.getComment(),
                rating.getRate(),
                rating.getUser().getFirstName()
        );
    }
}
