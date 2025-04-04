package com.example.productcrud.controller;

import com.example.productcrud.model.Payment;
import com.example.productcrud.model.PaymentRequest;
import com.example.productcrud.model.PaymentVerificationRequest;
import com.example.productcrud.model.PaymentResponse;
import com.example.productcrud.model.Subscriber;
import com.example.productcrud.model.TransactionHistory;
import com.example.productcrud.repository.SubscriberRepository;
import com.example.productcrud.repository.TransactionHistoryRepository;
import com.example.productcrud.service.EmailService;
import com.example.productcrud.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private EmailService emailService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest paymentRequest) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

            JSONObject response = new JSONObject();
            response.put("orderId", orderId);
            response.put("amount", paymentRequest.getAmount());
            response.put("key", razorpayKeyId);
            response.put("planId", paymentRequest.getPlanId());
            response.put("subscriberMobile", paymentRequest.getSubscriberMobile());

            return ResponseEntity.ok(response.toString());
        } catch (RazorpayException e) {
            return ResponseEntity.status(500).body("Error creating Razorpay order: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(@RequestBody PaymentVerificationRequest verificationRequest) {
        try {
            String orderId = verificationRequest.getRazorpayOrderId();
            String paymentId = verificationRequest.getRazorpayPaymentId();
            String signature = verificationRequest.getRazorpaySignature();
            String planId = verificationRequest.getPlanId();
            String subscriberMobile = verificationRequest.getSubscriberMobile();

            // Verify signature
            String generatedSignature = generateSignature(orderId + "|" + paymentId, razorpayKeySecret);
            if (!generatedSignature.equals(signature)) {
                return ResponseEntity.status(400).body(
                    new PaymentResponse(false, "Signature mismatch", null, null)
                );
            }

            // Fetch subscriber
            Subscriber subscriber = subscriberRepository.findByMobile(subscriberMobile);
            if (subscriber == null) {
                return ResponseEntity.status(404).body(
                    new PaymentResponse(false, "Subscriber not found", null, null)
                );
            }

            // Save transaction
            TransactionHistory transaction = new TransactionHistory();
            transaction.setSubscriber(subscriber);
            transaction.setPlanId(Integer.valueOf(planId));
            transaction.setPlanName(verificationRequest.getPlanName());
            transaction.setCategory(verificationRequest.getCategory());
            transaction.setDetails(verificationRequest.getDetails());
            transaction.setAmountPaid(verificationRequest.getAmount().doubleValue());
            transaction.setPaymentMode("Razorpay");
            transaction.setTransactionDate(LocalDateTime.now());

            TransactionHistory savedTransaction = transactionHistoryRepository.save(transaction);

            // Save PlanHistory
            Payment payment = new Payment(verificationRequest.getPlanName(), verificationRequest.getAmount());
            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            paymentService.savePayment(payment);
            paymentService.savePlanHistory(subscriberMobile, Integer.valueOf(planId), payment, verificationRequest.getAmount().doubleValue());

            // Send email confirmation
            emailService.sendPaymentConfirmationEmail(
                subscriber.getEmail(),
                subscriber.getName(),
                verificationRequest.getPlanName(),
                verificationRequest.getAmount().doubleValue()
            );

            return ResponseEntity.ok(
                new PaymentResponse(true, "Payment verified successfully", savedTransaction, null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                new PaymentResponse(false, "Error during verification: " + e.getMessage(), null, null)
            );
        }
    }

    private String generateSignature(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}