package jala.university.Qatu.controller;

import jala.university.Qatu.domain.user.dto.UserResponseDTO;
import jala.university.Qatu.domain.user.enums.UserRole;
import jala.university.Qatu.domain.user.enums.UserStatus;
import jala.university.Qatu.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private SellerController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSellers() {
        Page<UserResponseDTO> sellers = new PageImpl<>(Collections.emptyList());
        when(sellerService.getSellers(1, 10)).thenReturn(sellers);

        ResponseEntity<Page<UserResponseDTO>> response = controller.getAllSellers(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellers, response.getBody());
    }

    @Test
    public void testBanSellerSuccess() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.banSeller(sellerId)).thenReturn("Seller banned");

        ResponseEntity<String> response = controller.banSeller(sellerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Seller banned", response.getBody());
    }

    @Test
    public void testBanSellerNotFound() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.banSeller(sellerId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<String> response = controller.banSeller(sellerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testBanSellerInternalServerError() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.banSeller(sellerId)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<String> response = controller.banSeller(sellerId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testUnbanSellerSuccess() {
        String sellerId = UUID.randomUUID().toString();
        UserResponseDTO userResponseDTO = new UserResponseDTO(UUID.randomUUID(), "John", "Doe", 30, "john.doe@example.com", UserRole.SELLER);
        when(sellerService.unbanSeller(sellerId)).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = controller.unbanSeller(sellerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDTO, response.getBody());
    }

    @Test
    public void testUnbanSellerNotFound() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.unbanSeller(sellerId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserResponseDTO> response = controller.unbanSeller(sellerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUnbanSellerInternalServerError() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.unbanSeller(sellerId)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<UserResponseDTO> response = controller.unbanSeller(sellerId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testRevokeSellerSuccess() {
        String sellerId = UUID.randomUUID().toString();
        UserResponseDTO userResponseDTO = new UserResponseDTO(UUID.randomUUID(), "John", "Doe", 30, "john.doe@example.com", UserRole.USER);
        when(sellerService.turnSellerIntoUser(sellerId)).thenReturn(userResponseDTO);

        ResponseEntity<UserResponseDTO> response = controller.revokeSeller(sellerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDTO, response.getBody());
    }

    @Test
    public void testRevokeSellerUnauthorized() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.turnSellerIntoUser(sellerId)).thenThrow(new RuntimeException("User unauthorized"));

        ResponseEntity<UserResponseDTO> response = controller.revokeSeller(sellerId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRevokeSellerNotFound() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.turnSellerIntoUser(sellerId)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserResponseDTO> response = controller.revokeSeller(sellerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRevokeSellerInternalServerError() {
        String sellerId = UUID.randomUUID().toString();
        when(sellerService.turnSellerIntoUser(sellerId)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<UserResponseDTO> response = controller.revokeSeller(sellerId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetActiveSellers() {
        Page<UserResponseDTO> sellers = new PageImpl<>(Collections.emptyList());
        when(sellerService.getSellersByStatus(UserStatus.ACTIVE, 1, 10)).thenReturn(sellers);

        ResponseEntity<Page<UserResponseDTO>> response = controller.getActiveSellers(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellers, response.getBody());
    }

    @Test
    public void testGetBannedSellers() {
        Page<UserResponseDTO> sellers = new PageImpl<>(Collections.emptyList());
        when(sellerService.getSellersByStatus(UserStatus.INACTIVE, 1, 10)).thenReturn(sellers);

        ResponseEntity<Page<UserResponseDTO>> response = controller.getBannedSellers(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellers, response.getBody());
    }
}
