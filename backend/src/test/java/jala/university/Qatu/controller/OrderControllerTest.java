package jala.university.Qatu.controller;

import jala.university.Qatu.domain.order.Order;
import jala.university.Qatu.domain.order.dto.OrderResponseDTO;
import jala.university.Qatu.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllOrders() {
        Page<Order> orders = new PageImpl<>(Collections.emptyList());
        when(orderService.getAllOrders(0, 10)).thenReturn(orders);

        ResponseEntity<?> response = orderController.getAllOrders(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testCreateOrderFromCart() {
        OrderResponseDTO orderResponseDTO = OrderResponseDTO.builder().build();
        when(orderService.createOrderFromCart()).thenReturn(orderResponseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.createOrderFromCart();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderResponseDTO, response.getBody());
    }

    @Test
    public void testGetShippingCost() {
        UUID orderId = UUID.randomUUID();
        BigDecimal shippingCost = BigDecimal.valueOf(10.0);
        when(orderService.getShippingCost(orderId)).thenReturn(shippingCost);

        ResponseEntity<BigDecimal> response = orderController.getShippingCost(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shippingCost, response.getBody());
    }

    @Test
    public void testGetShippingCostOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderService.getShippingCost(orderId)).thenThrow(new RuntimeException("Order not found"));

        ResponseEntity<BigDecimal> response = orderController.getShippingCost(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetShippingCostUserUnauthorized() {
        UUID orderId = UUID.randomUUID();
        when(orderService.getShippingCost(orderId)).thenThrow(new RuntimeException("User unauthorized to get this order"));

        ResponseEntity<BigDecimal> response = orderController.getShippingCost(orderId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
