package com.bmsp.bmsp.controller.auth;

import com.bmsp.bmsp.dto.request.auth.*;
import com.bmsp.bmsp.dto.response.auth.JwtResponse;
import com.bmsp.bmsp.model.auth.ERole;
import com.bmsp.bmsp.model.auth.Role;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.Customer;
import com.bmsp.bmsp.model.Admin;
import com.bmsp.bmsp.repository.auth.RoleRepository;
import com.bmsp.bmsp.repository.auth.UserRepository;
import com.bmsp.bmsp.security.JwtUtils;
import com.bmsp.bmsp.security.UserDetailsImpl;
import com.bmsp.bmsp.service.AuthService;
import com.bmsp.bmsp.service.EmailService;
import com.bmsp.bmsp.util.OTPGenerator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthService authService;

    @Autowired
    EmailService emailService;

    @Autowired
    UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.isEmailVerified()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", "error",
                                "message", "Email not verified. Please verify your email first."
                        ));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles
            ));
        } catch (BadCredentialsException e) {
            logger.error("Login failed for user: {}", loginRequest.getEmail());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "error",
                            "message", "Invalid credentials"
                    ));
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Login failed. Please try again later."
                    ));
        }
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegisterRequest signUpRequest) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", "error",
                                "message", "Email is already in use!"
                        ));
            }

            // Check if phone number already exists
            if (userRepository.existsByPhone(signUpRequest.getPhone())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", "error",
                                "message", "Phone number is already in use!"
                        ));
            }

            User user = new Customer();
            user.setFullName(signUpRequest.getFullName());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(encoder.encode(signUpRequest.getPassword()));
            user.setPhone(signUpRequest.getPhone());

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = sdf.parse(signUpRequest.getDateOfBirth());
                user.setDateOfBirth(dob);
            } catch (ParseException e) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "status", "error",
                                "message", "Invalid date format. Use yyyy-MM-dd"
                        ));
            }

            user.setGender(signUpRequest.getGender());
            user.setStreetAddress(signUpRequest.getStreetAddress());
            user.setCity(signUpRequest.getCity());
            user.setState(signUpRequest.getState());
            user.setPostalCode(signUpRequest.getPostalCode());
            user.setCountry(signUpRequest.getCountry());

            Customer customer = (Customer) user;
            customer.setAccountType(signUpRequest.getAccountType());
            customer.setInitialDeposit(signUpRequest.getInitialDeposit());
            customer.setOccupation(signUpRequest.getOccupation());
            customer.setAadharNumber(signUpRequest.getAadharNumber());
            customer.setPanNumber(signUpRequest.getPanNumber());
            customer.setNomineeName(signUpRequest.getNomineeName());
            customer.setNomineeRelation(signUpRequest.getNomineeRelation());

            Set<Role> roles = new HashSet<>();
            Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Customer role not configured in system"));
            roles.add(customerRole);
            user.setRoles(roles);

            String otp = OTPGenerator.generateOTP(8);
            authService.saveOTP(user.getEmail(), otp);
            emailService.sendVerificationEmail(user.getEmail(), otp);

            userRepository.save(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "status", "success",
                            "message", "Customer registered successfully! Please verify your email with the OTP sent.",
                            "email", user.getEmail()
                    ));
        } catch (RuntimeException e) {
            logger.error("Customer registration failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Registration failed due to system error. Please contact support."
                    ));
        } catch (Exception e) {
            logger.error("Unexpected error during customer registration: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Registration failed. Please try again later."
                    ));
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminRegisterRequest signUpRequest) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", "error",
                                "message", "Email is already in use!"
                        ));
            }

            // Check if phone number already exists
            if (userRepository.existsByPhone(signUpRequest.getPhone())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", "error",
                                "message", "Phone number is already in use!"
                        ));
            }

            User user = new Admin();
            user.setFullName(signUpRequest.getFullName());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(encoder.encode(signUpRequest.getPassword()));
            user.setPhone(signUpRequest.getPhone());

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = sdf.parse(signUpRequest.getDateOfBirth());
                user.setDateOfBirth(dob);
            } catch (ParseException e) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "status", "error",
                                "message", "Invalid date format. Use yyyy-MM-dd"
                        ));
            }

            user.setGender(signUpRequest.getGender());
            user.setStreetAddress(signUpRequest.getStreetAddress());
            user.setCity(signUpRequest.getCity());
            user.setState(signUpRequest.getState());
            user.setPostalCode(signUpRequest.getPostalCode());
            user.setCountry(signUpRequest.getCountry());

            Admin admin = (Admin) user;
            admin.setEmployeeId(signUpRequest.getEmployeeId());
            admin.setDesignation(signUpRequest.getDesignation());
            admin.setBranchCode(signUpRequest.getBranchCode());
            admin.setBranchName(signUpRequest.getBranchName());
            admin.setAccessLevel(signUpRequest.getAccessLevel());

            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not configured in system"));
            roles.add(adminRole);
            user.setRoles(roles);

            String otp = OTPGenerator.generateOTP(8);
            authService.saveOTP(user.getEmail(), otp);
            emailService.sendVerificationEmail(user.getEmail(), otp);

            userRepository.save(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "status", "success",
                            "message", "Admin registered successfully! Please verify your email with the OTP sent.",
                            "email", user.getEmail()
                    ));
        } catch (RuntimeException e) {
            logger.error("Admin registration failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Registration failed due to system error. Please contact support."
                    ));
        } catch (Exception e) {
            logger.error("Unexpected error during admin registration: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Registration failed. Please try again later."
                    ));
        }
    }

   // AuthController.java (partial update)
@PostMapping("/verify-email")
public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyRequest) {
    try {
        ResponseEntity<?> response = authService.verifyEmail(verifyRequest.getEmail(), verifyRequest.getOtp());
        if (response.getStatusCode() == HttpStatus.OK) {
            // Fetch user and send welcome email
            User user = userRepository.findByEmail(verifyRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + verifyRequest.getEmail()));
            
            emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Email verified successfully!"
            ));
        }
        return response;
    } catch (Exception e) {
       
            logger.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Email verification failed. Please try again."
                    ));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestParam String email) {
        try {
            if (!userRepository.existsByEmail(email)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", "error",
                                "message", "Email not found!"
                        ));
            }

            String otp = OTPGenerator.generateOTP(8);
            authService.saveOTP(email, otp);
            emailService.sendVerificationEmail(email, otp);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "New OTP sent successfully!"
            ));
        } catch (Exception e) {
            logger.error("Failed to resend OTP: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to resend OTP. Please try again."
                    ));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "status", "error",
                                "message", "Invalid token format"
                        ));
            }

            String jwt = authHeader.substring(7);
            String username = jwtUtils.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                return ResponseEntity.ok(Map.of(
                        "status", "valid",
                        "user", userDetails.getUsername(),
                        "roles", userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                ));
            }
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "error",
                            "message", "Invalid token"
                    ));
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "error",
                            "message", "Token validation failed"
                    ));
        }
    }
}