package jala.university.Qatu.service;

import jakarta.transaction.Transactional;
import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.dto.UserResponseDTO;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;
import jala.university.Qatu.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Transactional
public class SellerService {
    private final UserRepository userRepository;

    public SellerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String banSeller(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        return "Seller banned";
    }


    public UserResponseDTO unbanSeller(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.ACTIVE);

        return ConversionService.fromEntityToOrderItemDTO(userRepository.save(user));
    }

    public UserResponseDTO turnSellerIntoUser(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                        .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(UserRole.USER);
        return ConversionService.fromEntityToOrderItemDTO(userRepository.save(user));
    }

    public Page<UserResponseDTO> getSellers(Integer pageNumber, Integer pageSize) {
        Page<User> sellers = userRepository.findAllByRoleAndStatus(
                UserRole.SELLER,
                UserStatus.ACTIVE,
                PageRequest.of(pageNumber - 1, pageSize)
        );

        return ConversionService.convertPageEntityToPageDTO(sellers);
    }

    public Page<UserResponseDTO> getSellersByStatus(UserStatus status, Integer pageNumber, Integer pageSize) {
        Page<User> sellers = userRepository.findAllByRoleAndStatus(
                UserRole.SELLER,
                status,
                PageRequest.of(pageNumber - 1, pageSize)
        );

        return sellers.map(ConversionService::fromEntityToOrderItemDTO);
    }
}