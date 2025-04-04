package com.example.productcrud.service;

import com.example.productcrud.model.Payment;
import com.example.productcrud.model.Payment.PaymentStatus;
import com.example.productcrud.model.Subscriber;
import com.example.productcrud.model.TransactionHistory;
import com.example.productcrud.model.PlanHistory;
import com.example.productcrud.model.Plan;
import com.example.productcrud.repository.PaymentRepository;
import com.example.productcrud.repository.SubscriberRepository;
import com.example.productcrud.repository.TransactionHistoryRepository;
import com.example.productcrud.repository.PlanRepository;
import com.example.productcrud.repository.PlanHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final SubscriberRepository subscriberRepository;
    private final PlanRepository planRepository;
    private final PlanHistoryRepository planHistoryRepository;

    public PaymentService(PaymentRepository paymentRepository, 
                          TransactionHistoryRepository transactionHistoryRepository,
                          SubscriberRepository subscriberRepository,
                          PlanRepository planRepository,
                          PlanHistoryRepository planHistoryRepository) {
        this.paymentRepository = paymentRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.subscriberRepository = subscriberRepository;
        this.planRepository = planRepository;
        this.planHistoryRepository = planHistoryRepository;
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePaymentStatus(Integer paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        payment.setPaymentStatus(status);
        return paymentRepository.save(payment);
    }

    public TransactionHistory saveTransaction(String phoneNumber, String planName, Integer planId, String category, 
                                              String details, Double amountPaid, String paymentMode) {
        Subscriber subscriber = subscriberRepository.findByMobile(phoneNumber);
        if (subscriber == null) {
            throw new RuntimeException("Subscriber not found with mobile: " + phoneNumber);
        }

        TransactionHistory transaction = new TransactionHistory(
            subscriber, planName, planId, category, details, amountPaid, paymentMode, LocalDateTime.now()
        );
        return transactionHistoryRepository.save(transaction);
    }

    // New method to save PlanHistory after a successful payment
    public PlanHistory savePlanHistory(String subscriberMobile, Integer planId, Payment payment, Double amount) {
        Subscriber subscriber = subscriberRepository.findByMobile(subscriberMobile);
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Plan not found with ID: " + planId));
        
        // Ensure payment has payName set (e.g., from TransactionHistory)
        if (payment.getPayName() == null) {
            payment.setPayName("Razorpay"); // Set default or fetch from context if available
            System.out.println("Warning: payName was null, set to default 'Razorpay'");
        }
        System.out.println("Payment PayName: " + payment.getPayName()); // Debug

        PlanHistory planHistory = new PlanHistory(subscriber, plan, payment, BigDecimal.valueOf(amount));
        return planHistoryRepository.save(planHistory);
    }
}