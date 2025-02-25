package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jala.university.Qatu.domain.user.application.SellerApplication;
import jala.university.Qatu.domain.user.enums.ApplicationStatus;
import jala.university.Qatu.service.SellerApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/seller-applications")
@Tag(name = "Seller Applications", description = "APIs for managing seller applications")
public class SellerApplicationController {
    private final SellerApplicationService applicationService;

    public SellerApplicationController(SellerApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @Operation(summary = "Apply to become a seller", description = "Endpoint for users to apply for seller status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<Object> apply() {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(applicationService.applyToBeASeller());
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged or invalid token");
            else if (e.getMessage().equals("User not found")) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else if (e.getMessage().equals("You already have a pending application")) return ResponseEntity.status(HttpStatus.CONFLICT).body("Pending active application");
            else if (e.getMessage().startsWith("You must wait ")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Approve or Reject Seller Application", description = "Approve or reject a seller application by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> processApplication(
            @PathVariable UUID id,
            @RequestParam boolean approve) {
        applicationService.approveOrRejectApplication(id, approve);
        String message = approve ? "Application approved successfully" : "Application rejected successfully";
        return ResponseEntity.ok(message);
    }

    @GetMapping
    @Operation(summary = "Get list of applications", description = "Get a paginated list of seller applications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<SellerApplication>> getAllApplications(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(applicationService.getAllApplications(pageNumber, pageSize));
    }


    @GetMapping("/pending")
    @Operation(summary = "Get pending seller applications", description = "Returns all pending seller applications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending applications retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<SellerApplication>> getPendingApplications(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(ApplicationStatus.PENDING, pageNumber, pageSize));
    }
}
