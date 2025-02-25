package jala.university.Qatu.service;

import jala.university.Qatu.domain.user.User;
import jala.university.Qatu.domain.user.dto.UserDTO;
import jala.university.Qatu.domain.user.dto.UserResponseDTO;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;
import jala.university.Qatu.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(UserDTO data) {
        User user = new User();
        BeanUtils.copyProperties(data, user);

        var existentEmail = userRepository.findByEmail(data.email());

        if (existentEmail.isPresent()) throw new RuntimeException("Email already exists");

        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(data.role());

        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public Page<UserResponseDTO> getAllUsers(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        return ConversionService.convertPageEntityToPageDTO(userRepository.findAll(pageable));
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public User updateUser(UUID id, UserDTO data) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = getUserById(getCurrentUserId());

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if ((currentUser.getRole() == UserRole.ADMIN && user.getRole() == UserRole.ADMIN) || currentUser.getId().equals(id)) {
                User userToUpdate = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (data.firstName() != null) {
                    userToUpdate.setFirstName(data.firstName());
                }
                if (data.lastName() != null) {
                    userToUpdate.setLastName(data.lastName());
                }
                if (data.age() != null) {
                    userToUpdate.setAge(data.age());
                }
                if (data.email() != null) {
                    userToUpdate.setEmail(data.email());
                }
                if (data.password() != null) {
                    userToUpdate.setPassword(data.password());
                }
                if (data.role() != null) {
                    userToUpdate.setRole(data.role());
                }

                return userRepository.save(userToUpdate);
            } else {
                throw new RuntimeException("User unauthorized");
            }
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

    public UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return user.getId();
        } else {
            throw new RuntimeException("User not authenticated");
        }
    }

}
