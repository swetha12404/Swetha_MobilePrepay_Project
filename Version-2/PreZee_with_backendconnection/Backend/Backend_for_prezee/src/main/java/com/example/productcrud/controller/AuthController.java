package com.example.productcrud.controller;

import com.example.productcrud.Security.JwtUtil;
import com.example.productcrud.model.AuthRequest;
import com.example.productcrud.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          OtpService otpService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
    }

    @PostMapping("/login")
    //@RequestBody convert Json data into concern authRequest object
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } 
        catch (Exception e) {
            System.out.println("User not found: " + username);
            return ResponseEntity.status(401).body("Invalid username or mobile number");
        }

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            if (password == null || password.isEmpty()) {
                System.out.println("Password missing for admin: " + username);
                return ResponseEntity.status(401).body("Password is required for admin login");
            }           
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
                System.out.println("Admin login successful: " + username);
            } catch (Exception e) {
                System.out.println("Admin login failed: " + username);
                return ResponseEntity.status(401).body("Invalid username or password for admin");
            }
        } else if (password != null && !password.isEmpty()) {
            System.out.println("Password provided for subscriber: " + username);
            return ResponseEntity.status(400).body("Subscribers not alowed!");
        }

        String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest) {
        String mobile = otpRequest.getMobile();
        if (mobile == null || mobile.isEmpty()) {
            return ResponseEntity.status(400).body("Mobile number is required");
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(mobile);
        } catch (Exception e) {
            System.out.println("Subscriber not found: " + mobile);
            return ResponseEntity.status(404).body("Subscriber not found with mobile: " + mobile);
        }

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            System.out.println("Admin attempted OTP: " + mobile);
            return ResponseEntity.status(400).body("Admins cannot use OTP login");
        }

        otpService.generateAndSendOtp(mobile);
        System.out.println("OTP sent to: " + mobile);
        return ResponseEntity.ok("OTP sent to " + mobile);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest otpVerificationRequest) {
        String mobile = otpVerificationRequest.getMobile();
        String otp = otpVerificationRequest.getOtp();

        if (mobile == null || mobile.isEmpty() || otp == null || otp.isEmpty()) {
            return ResponseEntity.status(400).body("Mobile number and OTP are required");
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(mobile);
        } catch (Exception e) {
            System.out.println("Subscriber not found: " + mobile);
            return ResponseEntity.status(404).body("Subscriber not found with mobile: " + mobile);
        }

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            System.out.println("Admin attempted OTP verification: " + mobile);
            return ResponseEntity.status(400).body("Admins cannot use OTP login");
        }

        if (otpService.verifyOtp(mobile, otp)) {
            String jwt = jwtUtil.generateToken(userDetails);
            System.out.println("OTP verified for: " + mobile);
            return ResponseEntity.ok(jwt);
        } else {
            System.out.println("Invalid OTP for: " + mobile);
            return ResponseEntity.status(400).body("Invalid OTP");
        }
    }
}

class OtpRequest {
    private String mobile;

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
}

class OtpVerificationRequest {
    private String mobile;
    private String otp;

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}