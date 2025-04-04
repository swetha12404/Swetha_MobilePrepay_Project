package com.example.productcrud.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
public class TransactionHistory {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transaction_id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Subscriber subscriber;

    private String planName;
    private Integer planId;
    private String category;
    private String details;
    private Double amountPaid;
    private String paymentMode;
    private LocalDateTime transactionDate = LocalDateTime.now();

    // Default constructor
    public TransactionHistory() {}

    // Parameterized constructor
    public TransactionHistory(Subscriber subscriber, String planName, Integer planId, String category, String details, 
                              Double amountPaid, String paymentMode, LocalDateTime transactionDate) {
        this.subscriber = subscriber;
        this.planName = planName;
        this.planId = planId;
        this.category = category;
        this.details = details;
        this.amountPaid = amountPaid;
        this.paymentMode = paymentMode;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters
    public Integer getTransaction_id() { return transaction_id; }
    public void setTransaction_id(Integer transaction_id) { this.transaction_id = transaction_id; }
    public Subscriber getSubscriber() { return subscriber; }
    public void setSubscriber(Subscriber subscriber) { this.subscriber = subscriber; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}