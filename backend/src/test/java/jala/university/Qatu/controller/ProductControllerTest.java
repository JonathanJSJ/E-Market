package jala.university.Qatu.controller;

import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.product.dto.CreateProductDTO;
import jala.university.Qatu.domain.product.dto.GetProductDTO;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.service.ProductService;
import jala.university.Qatu.service.UserService;
import jala.university.Qatu.service.exceptions.CustomError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductController productController;

    private UUID productId;
    private UUID userId;
    private CreateProductDTO createProductDTO;
    private GetProductDTO getProductDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productId = UUID.randomUUID();
        userId = UUID.randomUUID();
        createProductDTO = new CreateProductDTO();
        createProductDTO.setName("Test Product");
        getProductDTO = new GetProductDTO();
        getProductDTO.setName("Test Product");
        product = new Product();
    }

    @Test
    void testGetAllProducts_SellerIdNotFound() {
        when(userService.getUserById(userId)).thenReturn(null);

        ResponseEntity<?> response = productController.getAllProducts(userId, 0, 20, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Seller Id doesn't exist", response.getBody());
    }

    @Test
    void testGetAllProducts_Success() {
        Page<Product> productsPage = new PageImpl<>(Collections.singletonList(product));
        when(userService.getUserById(userId)).thenReturn(new User());
        when(productService.getAllProductsByUserId(eq(userId), any())).thenReturn(productsPage);
        when(productService.toDTO(product)).thenReturn(getProductDTO);

        ResponseEntity<?> response = productController.getAllProducts(userId, 0, 20, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetProductById_ProductNotFound() {
        when(productService.getProductById(productId)).thenReturn(null);

        ResponseEntity<?> response = productController.getProductById(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetProductById_Success() {
        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.toDTO(product)).thenReturn(getProductDTO);

        ResponseEntity<?> response = productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getProductDTO, response.getBody());
    }

    @Test
    void testCreateProduct_ProductExists() {
        when(userService.getCurrentUserId()).thenReturn(userId);
        when(productService.productExistsByNameAndUserId(createProductDTO.getName(), userId)).thenReturn(true);

        ResponseEntity<?> response = productController.createProduct(createProductDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(new CustomError("Product with this name already exists"), response.getBody());
    }

    @Test
    void testCreateProduct_Success() throws IOException {
        when(userService.getCurrentUserId()).thenReturn(userId);
        when(productService.productExistsByNameAndUserId(createProductDTO.getName(), userId)).thenReturn(false);
        when(productService.createProduct(createProductDTO)).thenReturn(product);
        when(productService.toDTO(product)).thenReturn(getProductDTO);

        ResponseEntity<?> response = productController.createProduct(createProductDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(getProductDTO, response.getBody());
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        when(productService.getProductById(productId)).thenReturn(null);

        ResponseEntity<GetProductDTO> response = productController.updateProduct(productId, createProductDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateProduct_Success() {
        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.toDTO(product)).thenReturn(getProductDTO);

        ResponseEntity<GetProductDTO> response = productController.updateProduct(productId, createProductDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getProductDTO, response.getBody());
    }

    @Test
    void testDeleteProduct_Success() {
        doNothing().when(productService).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetProducts_WithFilters_Success() {
        Page<GetProductDTO> productsPage = new PageImpl<>(Collections.singletonList(getProductDTO));
        when(productService.createSpecification(any(), any(), any(), any(), any(), any())).thenReturn(null);
        when(productService.getAllProducts(any(), any())).thenReturn(productsPage);

        ResponseEntity<Page<GetProductDTO>> response = productController.getProducts(0, 20, "test", 10.0, 100.0, "category", 1.0, 5.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateProduct_UserNotFound() {
        when(userService.getCurrentUserId()).thenReturn(null);

        ResponseEntity<?> response = productController.createProduct(createProductDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        doThrow(new IllegalArgumentException("Product not found")).when(productService).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_NotOwner() {
        // Simule uma falha na verificação de propriedade
        doThrow(new SecurityException("You are not the owner of this product")).when(productService).deleteProduct(productId);

        ResponseEntity<?> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
