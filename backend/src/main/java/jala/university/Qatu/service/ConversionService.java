package jala.university.Qatu.service;

import jala.university.Qatu.domain.cart.dto.CartItemDTO;
import jala.university.Qatu.domain.chat.Chat;
import jala.university.Qatu.domain.chat.dto.ChatDTO;
import jala.university.Qatu.domain.chat.dto.MessageDTO;
import jala.university.Qatu.domain.chat.dto.UserChatDTO;
import jala.university.Qatu.domain.order.Order;
import jala.university.Qatu.domain.order.OrderItem;
import jala.university.Qatu.domain.order.dto.OrderItemDTO;
import jala.university.Qatu.domain.order.dto.OrderResponseDTO;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.dto.UserDTO;
import jala.university.Qatu.domain.user.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

public class ConversionService {

    public static UserResponseDTO fromEntityToOrderItemDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(), user.getEmail(),
                user.getRole()
        );
    }

    public static UserDTO fromEntityToDTO(User user) {
        return new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getAge(), user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    public static UserResponseDTO fromEntityToResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getAge(), user.getEmail(), user.getRole());
    }

    public static OrderItemDTO fromEntityToOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .shipping(item.getShippingPrice())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    public static OrderResponseDTO fromEntityToOrderResponse(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .user(fromEntityToDTO(order.getUser()))
                .shipping(order.getShipping())
                .items(order.getItems().stream()
                        .map(ConversionService::fromEntityToOrderItemDTO)
                        .collect(Collectors.toList()))
                .status(order.getOrderStatus())
                .subtotal(order.getSubtotal())
                .total((order.getTotal()))
                .createdAt(order.getCreatedAt())
                .build();
    }


    public static OrderItem convertCartItemsToOrderItems(CartItemDTO cartItem, Order order, Product product) {
        return OrderItem.builder()
                        .product(product)
                        .order(order)
                        .shippingPrice(cartItem.getShippingPrice())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .build();
    }

    public static ChatDTO convertToDto(Chat chat) {
        List<MessageDTO> messages = chat.getMessages().stream()
                .map(message -> new MessageDTO(new UserChatDTO(message.getSender().getId(), message.getSender().getFirstName()), message.getMessage()))
                .collect(Collectors.toList());

        UserChatDTO user = new UserChatDTO(chat.getUser().getId(), chat.getUser().getFirstName());
        UserChatDTO seller = new UserChatDTO(chat.getSeller().getId(), chat.getSeller().getFirstName());

        return new ChatDTO(chat.getId().toString(), user, seller, messages, chat.getChatStatus());
    }

    public static Page<UserResponseDTO> convertPageEntityToPageDTO(Page<User> page) {
        List<UserResponseDTO> dtos = page.getContent()
                .stream()
                .map(ConversionService::fromEntityToResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                dtos,
                page.getPageable(),
                page.getTotalElements()
        );
    }

}
