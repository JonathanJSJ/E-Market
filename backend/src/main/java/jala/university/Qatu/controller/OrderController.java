package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jala.university.Qatu.domain.order.dto.OrderResponseDTO;
import jala.university.Qatu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@Tag(name = "Order Controller", description = "Endpoints for managing orders.")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get All orders", description = "Retrieve all orders in the system with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
            ){
        return ResponseEntity.ok(orderService.getAllOrders(pageNumber, pageSize));
    }

    @PostMapping("/create-from-cart")
    @Operation(summary = "Create order from cart", description = "Creates a new order using items from the current user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid cart state or empty cart"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<OrderResponseDTO> createOrderFromCart() {
        OrderResponseDTO order = orderService.createOrderFromCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/shipping/{id}")
    @Operation(summary = "Get the shipping value", description = "get a shipping cost for this user, using the shipping price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shipping received"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<BigDecimal> getShippingCost(@PathVariable UUID id) {
        try {
            BigDecimal shippingCost = orderService.getShippingCost(id);
            return ResponseEntity.status(HttpStatus.OK).body(shippingCost);
        } catch (Exception e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            if (e.getMessage().equals("Order not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            if (e.getMessage().equals("User unauthorized to get this order")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order status", description = "Update order status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable UUID id, @RequestParam String status) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(id, status.toUpperCase()));
        } catch (Exception e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            if (e.getMessage().equals("Order not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            if (e.getMessage().equals("User unauthorized to get this order")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
