package jala.university.Qatu.controller;

import jala.university.Qatu.domain.cart.dto.AddToCartDTO;
import jala.university.Qatu.domain.cart.dto.CartResponseDTO;
import jala.university.Qatu.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartController = new CartController(cartService);
    }

    @Test
    void addItemToCart_Success() {
        // Arrange
        AddToCartDTO dto = new AddToCartDTO();
        dto.setProductId(UUID.randomUUID());
        dto.setQuantity(2);

        CartResponseDTO responseDTO = CartResponseDTO.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .items(Collections.emptyList())
                .total(BigDecimal.valueOf(50))
                .updatedAt(LocalDateTime.now())
                .build();

        when(cartService.addItemToCart(any(AddToCartDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<CartResponseDTO> response = cartController.addItemToCart(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(cartService, times(1)).addItemToCart(dto);
    }

    @Test
    void removeItemFromCart_Success() {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Act
        cartController.removeItemFromCart(productId);

        // Assert
        verify(cartService, times(1)).removeItemFromCart(productId);
    }
    @Test
    void getUserCart_Success() {
        // Arrange
        CartResponseDTO responseDTO = CartResponseDTO.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .items(Collections.emptyList())
                .total(BigDecimal.valueOf(100))
                .updatedAt(LocalDateTime.now())
                .build();

        when(cartService.getCurrentUserCart()).thenReturn(responseDTO);

        // Act
        ResponseEntity<CartResponseDTO> response = cartController.getUserCart();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(cartService, times(1)).getCurrentUserCart();
    }
}
