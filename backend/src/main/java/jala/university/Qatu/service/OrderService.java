package jala.university.Qatu.service;

import jakarta.transaction.Transactional;
import jala.university.Qatu.domain.cart.CartItem;
import jala.university.Qatu.domain.cart.dto.CartItemDTO;
import jala.university.Qatu.domain.cart.dto.CartResponseDTO;
import jala.university.Qatu.domain.order.*;
import jala.university.Qatu.domain.order.dto.CreateOrderDTO;
import jala.university.Qatu.domain.order.dto.OrderResponseDTO;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.repository.OrderRepository;
import jala.university.Qatu.repository.ProductRepository;
import jala.university.Qatu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    public Page<Order> getAllOrders(Integer pageNumber, Integer pageSize) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            return orderRepository.findAllByUserEmail(email, pageable);
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    @Transactional
    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();

        order.setUser(currentUser);
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT);

        for (CartItem itemDTO : createOrderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Product not found with id: " + itemDTO.getProduct().getId()));

            if (product.getAvailableQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.addItem(orderItem);

            product.setAvailableQuantity(product.getAvailableQuantity() - itemDTO.getQuantity());
            productRepository.save(product);
        }

        return ConversionService.fromEntityToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO createOrderFromCart() {
        CartResponseDTO cart = cartService.getCurrentUserCart();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        Order order = orderRepository.save(new Order());

        for (CartItemDTO item : cart.getItems()) {
            orderItemList.add(ConversionService.convertCartItemsToOrderItems(item, order, productRepository.findById(item.getProductId()).orElseThrow(() -> new RuntimeException("User not found"))));
        }

        BigDecimal shippingPrice = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItemList) {
            shippingPrice = shippingPrice.add(orderItem.getShippingPrice());
        }

        order = Order.builder()
                .id(order.getId())
                .user(currentUser)
                .items(orderItemList)
                .shipping(shippingPrice)
                .total(cart.getTotal())
                .orderStatus(OrderStatus.PREPARING)
                .createdAt(LocalDateTime.now())
                .build();

        order.calculateSubTotal();
        order.calculateTotal();

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart();

        return ConversionService.fromEntityToOrderResponse(savedOrder);
    }

    public BigDecimal getShippingCost(UUID orderId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("User unauthorized to get this order");
        }
        return order.getShipping();
    }

    public OrderResponseDTO updateOrderStatus(UUID id, String status) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("User unauthorized to get this order");
        }

        order.setOrderStatus(OrderStatus.valueOf(status));

        return ConversionService.fromEntityToOrderResponse(orderRepository.save(order));
    }
}
