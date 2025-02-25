package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.product.dto.CreateProductDTO;
import jala.university.Qatu.domain.product.dto.GetProductDTO;
import jala.university.Qatu.domain.product.dto.ProductVisitDTO;
import jala.university.Qatu.service.ProductService;
import jala.university.Qatu.service.UserService;
import jala.university.Qatu.service.exceptions.CustomError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "Endpoints for managing products. These endpoints are accessible everyone, except the edition mode for administrators and sellers")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @Operation(summary = "Get All Products", description = "Retrieve all products in the system with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Seller Id doesn't exist"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/seller/{id}", params = {"pageNumber", "pageSize"})
    public ResponseEntity<?> getAllProducts(
            @PathVariable @Parameter(description = "UUID of the seller") UUID id,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "sort", required = false) String sort) {

        if (userService.getUserById(id) == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Seller Id doesn't exist");

        Sort sorting = Sort.by(Sort.Direction.ASC, "id");

        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String field = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sorting = Sort.by(direction, field);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sorting);
        Page<Product> page = productService.getAllProductsByUserId(id, pageable);
        return ResponseEntity.ok(page.map(productService::toDTO));
    }

    @Operation(summary = "Get Products with Filters", description = "Retrieve products with filters and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<Page<GetProductDTO>> getProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "minRate", required = false) Double minRate,
            @RequestParam(value = "maxRate", required = false) Double maxRate
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Product> spec = productService.createSpecification(name, category, maxPrice, minPrice, maxRate, minRate);

        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts(pageable, spec));
    }

    @Operation(summary = "Get Product by ID", description = "Retrieve a product by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GetProductDTO> getProductById(@PathVariable @Parameter(description = "UUID of the product") UUID id) {
        Product product = productService.getProductById(id);
        if (product == null)
            return ResponseEntity.status(404).build();
        return ResponseEntity.ok(productService.toDTO(product));
    }

    @Operation(summary = "Get Product by ID for the product view, and increase the visit number.", description = "Return the product by id, for viewport")
    @GetMapping("/visit/{id}")
    public ResponseEntity<ProductVisitDTO> addVisit(@PathVariable @Parameter(description = "UUID of the product") UUID id) {
        ProductVisitDTO productVisitDTO = productService.addVisit(id);
        if (productVisitDTO == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(productVisitDTO);
    }

    @Operation(summary = "Get Products Recommendation", description = "Return all the products for the home screen")
    @GetMapping("/recommendation")
    public ResponseEntity getProductRecommendation(
            @Positive @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "category", required = false) String category
    ) {
        if (category == null) {
            List<GetProductDTO> products = productService.getProductRecommendation(pageSize);
            return ResponseEntity.ok(products);
        } else {
            Map<String, List<GetProductDTO>> products = productService.getProductRecommendation(pageSize, category);
            return ResponseEntity.ok(products);
        }
    }

    @Operation(summary = "Create New Product", description = "Create a new product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "The category does not exists",
                    content = @Content(schema = @Schema(implementation = CustomError.class)))
    })
    @PostMapping
    public ResponseEntity createProduct(@Valid @RequestBody CreateProductDTO dto) {
        UUID userId = userService.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user doesn't exist");
        }

        if (productService.productExistsByNameAndUserId(dto.getName(), userId)) {
            return ResponseEntity.status(409).body(new CustomError("Product with this name already exists"));
        }

        try {
            Product product = productService.createProduct(dto);
            GetProductDTO productDTO = productService.toDTO(product);
            return ResponseEntity.status(201).body(productDTO);
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.status(404).body(new CustomError(e.getMessage()));
        }
    }

    @Operation(summary = "Update Existing Product", description = "Update an existing product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER') and @productService.isProductOwner(#id, principal.username)")
    public ResponseEntity<GetProductDTO> updateProduct(@PathVariable @Parameter(description = "UUID of the product to be updated") UUID id,
                                        @Valid @RequestBody @Parameter(description = "Details of the product to be updated") CreateProductDTO dto) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null)
            return ResponseEntity.notFound().build();

        productService.updateProduct(existingProduct.getId(), dto);
        return ResponseEntity.ok(productService.toDTO(existingProduct));
    }

    @Operation(summary = "Delete Product", description = "Delete a product from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SELLER') and @productService.isProductOwner(#id, principal.username)")
    public ResponseEntity deleteProduct(@PathVariable @Parameter(description = "UUID of the product to be deleted") UUID id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(new CustomError(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new CustomError(e.getMessage()));
        }
    }
}
