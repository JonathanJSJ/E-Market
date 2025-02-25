package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jala.university.Qatu.domain.user.dto.UserResponseDTO;
import jala.university.Qatu.domain.user.enums.UserStatus;
import jala.university.Qatu.service.SellerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@Tag(name = "Seller Management", description = "APIs for seller-related operations")
public class SellerController {
    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    @Operation(summary = "List of sellers", description = "Returns all active sellers of Qatu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller list received"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<Page<UserResponseDTO>> getAllSellers(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(sellerService.getSellers(pageNumber, pageSize));
    }

    @PostMapping("/ban/{id}")
    @Operation(summary = "Ban a seller", description = "Used when a seller violates Qatu's rules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller banned"),
            @ApiResponse(responseCode = "404", description = "Seller not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> banSeller(@PathVariable String id) {
        try {
            return ResponseEntity.ok(sellerService.banSeller(id));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/unban/{id}")
    @Operation(summary = "Remove the ban from seller", description = "Used when a the admin wants to unban the seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller unbanned"),
            @ApiResponse(responseCode = "404", description = "Seller not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDTO> unbanSeller(@PathVariable String id) {
        try {
            return ResponseEntity.ok(sellerService.unbanSeller(id));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/revoke/{id}")
    @Operation(summary = "Seller turns into a user", description = "When a seller closes their store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seller status changed"),
            @ApiResponse(responseCode = "404", description = "Seller not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<UserResponseDTO> revokeSeller(@PathVariable String id) {
        try {
            return ResponseEntity.ok(sellerService.turnSellerIntoUser(id));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated") || (e.getMessage().equals("User unauthorized"))) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @Operation(summary = "List of active sellers", description = "Returns all approved and active sellers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active sellers list received"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<Page<UserResponseDTO>> getActiveSellers(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(sellerService.getSellersByStatus(UserStatus.ACTIVE, pageNumber, pageSize));
    }

    @GetMapping("/banned")
    @Operation(summary = "List of banned sellers", description = "Returns all banned sellers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Banned sellers list received"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getBannedSellers(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(sellerService.getSellersByStatus(UserStatus.INACTIVE, pageNumber, pageSize));
    }
}
