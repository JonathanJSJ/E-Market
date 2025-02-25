package jala.university.Qatu.service;

import jakarta.transaction.Transactional;
import jala.university.Qatu.domain.cart.dto.AddToCartDTO;
import jala.university.Qatu.domain.cart.Cart;
import jala.university.Qatu.domain.cart.CartItem;
import jala.university.Qatu.domain.cart.dto.CartInfoDTO;
import jala.university.Qatu.domain.cart.dto.CartResponseDTO;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.repository.CartRepository;
import jala.university.Qatu.repository.ProductRepository;
import jala.university.Qatu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartResponseDTO addItemToCart(AddToCartDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = findProduct(dto.getProductId());

        if (product.getAvailableQuantity() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getAvailableQuantity());
        }

        Cart cart = getOrCreateCart(currentUser);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(dto.getProductId()))
                .findFirst();

        double randomValue = ThreadLocalRandom.current().nextDouble(5, 50);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + dto.getQuantity();

            if (product.getAvailableQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock. Available: " + product.getAvailableQuantity());
            }

            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(dto.getQuantity())
                    .shipping(BigDecimal.ZERO)
                    .build();
            cart.addItem(newItem);

            boolean sellerHasShipping = cart.getItems().stream()
                    .anyMatch(item -> item.getProduct().getUser().getId().equals(newItem.getProduct().getUser().getId())
                            && item.getShipping() != null
                            && item.getShipping().compareTo(BigDecimal.ZERO) > 0);

            if (!sellerHasShipping) {
                newItem.setShipping(BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP));
            } else {
                newItem.setShipping(BigDecimal.ZERO);
            }
        }

        cart = cartRepository.save(cart);
        return CartResponseDTO.fromEntity(cart);
    }

    public void removeItemFromCart(UUID productId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = getOrCreateCart(currentUser);
        Optional<CartItem> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemToRemove.isEmpty()) {
            throw new RuntimeException("Item not found in cart");
        }

        CartItem item = itemToRemove.get();
        BigDecimal shippingToTransfer = item.getShipping().compareTo(BigDecimal.ZERO) > 0 ? item.getShipping() : BigDecimal.ZERO;

        cart.getItems().remove(item);

        if (shippingToTransfer.compareTo(BigDecimal.ZERO) > 0) {
            Optional<CartItem> otherItem = cart.getItems().stream()
                    .filter(i -> i.getProduct().getUser().getId().equals(item.getProduct().getUser().getId()) && i.getShipping().compareTo(BigDecimal.ZERO) == 0)
                    .findFirst();

            if (otherItem.isPresent()) {
                otherItem.get().setShipping(shippingToTransfer);
            }
        }

        cartRepository.save(cart);
    }

    public CartResponseDTO getCurrentUserCart() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = findUserCart(currentUser);
        return CartResponseDTO.fromEntity(cart);
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public void clearCart() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = findUserCart(currentUser);

        cart.getItems().clear();
    }

    public CartResponseDTO updateItemQuantity(UUID id, Integer quantity) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = findUserCart(currentUser);

        boolean itemFound = false;

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(id)) {
                item.setQuantity(quantity);
                itemFound = true;
                break;
            }
        }

        return itemFound ? CartResponseDTO.fromEntity(cartRepository.save(cart)) : null;
    }

    public CartInfoDTO getCartPriceInformation() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = ((UserDetails) principal).getUsername();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = findUserCart(currentUser);

        CartInfoDTO cartInfoDTO = new CartInfoDTO();
        cartInfoDTO.setShippingCost(calculateTotalCartShipping(cart));
        cartInfoDTO.setSubTotal(calculateTotalCartPrice(cart));
        cartInfoDTO.setTotalPrice(cartInfoDTO.getSubTotal().add(cartInfoDTO.getShippingCost()));

        return cartInfoDTO;
    }

    private BigDecimal calculateTotalCartPrice(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        return total;
    }

    private BigDecimal calculateTotalCartShipping(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            total = total.add(item.getShipping());
        }
        return total;
    }

    private Cart findUserCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for current user"));
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
    }
}

