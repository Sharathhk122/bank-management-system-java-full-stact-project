package com.bmsp.bmsp.controller.loan;

import com.bmsp.bmsp.dto.request.loan.LoanRequest;
import com.bmsp.bmsp.dto.request.loan.EMIPaymentRequest;
import com.bmsp.bmsp.dto.response.loan.LoanResponse;
import com.bmsp.bmsp.dto.response.loan.EMIScheduleResponse;
import com.bmsp.bmsp.exception.ErrorResponse;
import com.bmsp.bmsp.exception.InsufficientBalanceException;
import com.bmsp.bmsp.exception.LoanException;
import com.bmsp.bmsp.exception.ResourceNotFoundException;
import com.bmsp.bmsp.exception.UnauthorizedAccessException;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.security.UserDetailsImpl;
import com.bmsp.bmsp.service.loan.LoanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<?> applyForLoan(
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {
        try {
            logger.info("Received loan application request: {}", request);
            User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            LoanResponse response = loanService.applyForLoan(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error processing loan application", e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<?> getLoanDetails(
            @PathVariable Long loanId,
            Authentication authentication) {
        try {
            User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            LoanResponse response = loanService.getLoanDetails(loanId, user);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching loan details for loanId: {}", loanId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserLoans(Authentication authentication) {
        try {
            User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            return ResponseEntity.ok(loanService.getUserLoans(user));
        } catch (Exception e) {
            logger.error("Error fetching user loans", e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingLoans() {
        try {
            return ResponseEntity.ok(loanService.getPendingLoans());
        } catch (Exception e) {
            logger.error("Error fetching pending loans", e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{loanId}/approve")
    public ResponseEntity<?> approveLoan(
            @PathVariable Long loanId,
            Authentication authentication) {
        try {
            User adminUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            LoanResponse response = loanService.approveLoan(loanId, adminUser);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (LoanException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error approving loan with ID: {}", loanId, e);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to approve loan"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{loanId}/reject")
    public ResponseEntity<?> rejectLoan(
            @PathVariable Long loanId,
            @RequestParam String rejectionReason,
            Authentication authentication) {
        try {
            User adminUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            LoanResponse response = loanService.rejectLoan(loanId, rejectionReason, adminUser);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (LoanException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rejecting loan with ID: {}", loanId, e);
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to reject loan"));
        }
    }

   @PostMapping("/{loanId}/pay-emi")
    public ResponseEntity<?> payEMI(
            @PathVariable Long loanId,
            @RequestBody @Valid EMIPaymentRequest request,
            Authentication authentication) {
        try {
            User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            String response = loanService.payEMI(loanId, request.getInstallmentNumber(), user);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", response);
                put("timestamp", LocalDateTime.now().toString());
            }});
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (LoanException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error processing EMI payment for loan ID: {}, installment: {}", 
                loanId, request.getInstallmentNumber(), e);
            return ResponseEntity.internalServerError().body(new ErrorResponse(
                "Failed to process EMI payment: " + e.getMessage()));
        }
    }

    @GetMapping("/{loanId}/schedule")
    public ResponseEntity<?> getEMISchedule(
            @PathVariable Long loanId,
            Authentication authentication) {
        try {
            User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            List<EMIScheduleResponse> response = loanService.getEMISchedule(loanId, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching EMI schedule for loan ID: {}", loanId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        logger.warn("Validation errors: {}", errors);
        return errors;
    }
}