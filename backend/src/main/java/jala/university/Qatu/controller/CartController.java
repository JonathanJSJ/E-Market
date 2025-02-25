package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jala.university.Qatu.domain.cart.Cart;
import jala.university.Qatu.domain.cart.dto.AddToCartDTO;
import jala.university.Qatu.domain.cart.dto.CartInfoDTO;
import jala.university.Qatu.domain.cart.dto.CartResponseDTO;
import jala.university.Qatu.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Add a product at the cart", description = "Add a item at the list of products into the user cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "401", description = "User not logged"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/item")
        @ResponseStatus(HttpStatus.CREATED)
        public ResponseEntity<CartResponseDTO> addItemToCart(@RequestBody @Valid AddToCartDTO dto) {
            try {
                return ResponseEntity.status(HttpStatus.OK).body(cartService.addItemToCart(dto));
            } catch(RuntimeException e) {
                if (e.getMessage().startsWith("Insufficient stock. Available")) return ResponseEntity.status(HttpStatus.CONFLICT).build();
                else if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    @Operation(summary = "Remove an item from the cart", description = "Remove an item from the user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Item not found in the cart"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> removeItemFromCart(@PathVariable UUID productId) {
        try {
            cartService.removeItemFromCart(productId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else if (e.getMessage().equals("Item not found in cart")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get the user cart", description = "get all items and the cart information from the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<CartResponseDTO> getUserCart() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(cartService.getCurrentUserCart());
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else if (e.getMessage().equals("Cart not found for current user")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Item quantity updated", description = "It update the item quantity of an item in the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found in the cart"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateItemQuantity(@PathVariable UUID productId, @RequestParam Integer quantity) {
        CartResponseDTO cart = cartService.updateItemQuantity(productId, quantity);

        return cart != null ? ResponseEntity.status(HttpStatus.OK).body(cart) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get values from this cart", description = "It returns the total, subtotal and the shipping price for this cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart found successfully"),
            @ApiResponse(responseCode = "401", description = "User not logged"),
            @ApiResponse(responseCode = "404", description = "Cart empty"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/info")
    public ResponseEntity<CartInfoDTO> getCartPriceInformation() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartPriceInformation());
        } catch (NullPointerException e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else if (e.getMessage().equals("Cart not found for current user")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
