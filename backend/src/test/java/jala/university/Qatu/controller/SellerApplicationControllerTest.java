package jala.university.Qatu.controller;

import jala.university.Qatu.domain.user.application.SellerApplication;
import jala.university.Qatu.domain.user.enums.ApplicationStatus;
import jala.university.Qatu.service.SellerApplicationService;
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
import static org.mockito.Mockito.*;

public class SellerApplicationControllerTest {

    @Mock
    private SellerApplicationService applicationService;

    @InjectMocks
    private SellerApplicationController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testApplySuccess() {
        SellerApplication application = new SellerApplication();
        when(applicationService.applyToBeASeller()).thenReturn(application);

        ResponseEntity<Object> response = controller.apply();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(application, response.getBody());
    }

    @Test
    public void testApplyUserNotAuthenticated() {
        when(applicationService.applyToBeASeller()).thenThrow(new RuntimeException("User not authenticated"));

        ResponseEntity<Object> response = controller.apply();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not logged or invalid token", response.getBody());
    }

    @Test
    public void testApplyUserNotFound() {
        when(applicationService.applyToBeASeller()).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<Object> response = controller.apply();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testApplyPendingApplication() {
        when(applicationService.applyToBeASeller()).thenThrow(new RuntimeException("You already have a pending application"));

        ResponseEntity<Object> response = controller.apply();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Pending active application", response.getBody());
    }

    @Test
    public void testApplyWaitPeriod() {
        when(applicationService.applyToBeASeller()).thenThrow(new RuntimeException("You must wait 5 more days before submitting a new application"));

        ResponseEntity<Object> response = controller.apply();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You must wait 5 more days before submitting a new application", response.getBody());
    }

    @Test
    public void testApplyInternalServerError() {
        when(applicationService.applyToBeASeller()).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<Object> response = controller.apply();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testProcessApplicationApprove() {
        UUID applicationId = UUID.randomUUID();
        doNothing().when(applicationService).approveOrRejectApplication(applicationId, true);

        ResponseEntity<String> response = controller.processApplication(applicationId, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Application approved successfully", response.getBody());
    }

    @Test
    public void testProcessApplicationReject() {
        UUID applicationId = UUID.randomUUID();
        doNothing().when(applicationService).approveOrRejectApplication(applicationId, false);

        ResponseEntity<String> response = controller.processApplication(applicationId, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Application rejected successfully", response.getBody());
    }

    @Test
    public void testGetAllApplications() {
        Page<SellerApplication> applications = new PageImpl<>(Collections.emptyList());
        when(applicationService.getAllApplications(1, 10)).thenReturn(applications);

        ResponseEntity<Page<SellerApplication>> response = controller.getAllApplications(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applications, response.getBody());
    }

    @Test
    public void testGetPendingApplications() {
        Page<SellerApplication> applications = new PageImpl<>(Collections.emptyList());
        when(applicationService.getApplicationsByStatus(ApplicationStatus.PENDING, 1, 10)).thenReturn(applications);

        ResponseEntity<Page<SellerApplication>> response = controller.getPendingApplications(1, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applications, response.getBody());
    }
}
