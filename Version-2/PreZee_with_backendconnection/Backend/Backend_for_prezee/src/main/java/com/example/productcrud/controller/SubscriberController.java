package com.example.productcrud.controller;

import com.example.productcrud.model.Plan;
import com.example.productcrud.model.PlanHistory;
import com.example.productcrud.model.Subscriber;
import com.example.productcrud.model.TransactionHistory;
import com.example.productcrud.model.Payment; // Add this import
import com.example.productcrud.service.EmailService;
import com.example.productcrud.service.PlanHistoryService;
import com.example.productcrud.service.PlanService;
import com.example.productcrud.service.SubscriberService;
import com.example.productcrud.service.TransactionHistoryService;
import com.example.productcrud.service.PaymentService; // Add this import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal; // Add this import

@RestController
@RequestMapping("/api/subscriber")
@PreAuthorize("hasRole('SUBSCRIBER')")
public class SubscriberController {

    private final SubscriberService subscriberService;
    private final PlanService planService;
    
    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private PlanHistoryService planHistoryService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentService paymentService; // Add this field

    public SubscriberController(SubscriberService subscriberService, PlanService planService) {
        this.subscriberService = subscriberService;
        this.planService = planService;
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Subscriber> registerSubscriber(@RequestBody Subscriber subscriber) {
        if (subscriber.getMobile() == null || subscriber.getName() == null || 
            subscriber.getEmail() == null || subscriber.getPassword() == null) {
            throw new IllegalArgumentException("Mobile, name, email, and password are required.");
        }
        return ResponseEntity.ok(subscriberService.saveSubscriber(subscriber));
    }

    @GetMapping("/plans")
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @GetMapping("/profile")
    public ResponseEntity<Subscriber> getProfile() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        Subscriber subscriber = subscriberService.findByMobile(mobile);
        if (subscriber == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(subscriber);
    }

    @PutMapping("/profile")
    public ResponseEntity<Subscriber> updateProfile(@RequestBody Subscriber updatedSubscriber) {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        Subscriber existingSubscriber = subscriberService.findByMobile(mobile);
        if (existingSubscriber == null) {
            return ResponseEntity.status(404).build();
        }

        // Update editable fields
        existingSubscriber.setName(updatedSubscriber.getName());
        existingSubscriber.setEmail(updatedSubscriber.getEmail());

        Subscriber savedSubscriber = subscriberService.saveSubscriber(existingSubscriber);
        return ResponseEntity.ok(savedSubscriber);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionHistory>> getTransactions() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TransactionHistory> transactions = transactionHistoryService.findBySubscriberMobile(mobile);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/plan-history")
    public ResponseEntity<List<PlanHistory>> getPlanHistory() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PlanHistory> planHistory = planHistoryService.findBySubscriberMobile(mobile);
        System.out.println("Plan History for " + mobile + ": " + planHistory); // Debug
        return ResponseEntity.ok(planHistory);
    }

    // New payment processing endpoint
    @PostMapping("/process-payment")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        // Create and populate Payment object
        Payment payment = new Payment();
        payment.setPayName(request.getPaymentMode()); // e.g., "Razorpay"
        payment.setAmount(BigDecimal.valueOf(request.getAmount()));
        
        // Save payment and plan history
        paymentService.savePayment(payment);
        paymentService.savePlanHistory(request.getMobile(), request.getPlanId(), payment, request.getAmount());
        
        return ResponseEntity.ok("Payment processed successfully");
    }
}

// Define PaymentRequest class (can be moved to a separate file)
class PaymentRequest {
    private String mobile;
    private Integer planId;
    private Double amount;
    private String paymentMode;

    // Getters and Setters
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
}