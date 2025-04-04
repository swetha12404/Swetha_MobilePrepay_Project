package com.example.productcrud.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_history")
public class PlanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recharge_id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Subscriber subscriber;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private Double amount;
    private String payment_mode;
    private LocalDateTime validity_start;
    private LocalDateTime validity_end;
    private LocalDateTime recharge_date = LocalDateTime.now();

    // Default constructor
    public PlanHistory() {}

    // Existing parameterized constructor
    public PlanHistory(Subscriber subscriber, Plan plan, Double amount, String payment_mode, 
                       LocalDateTime validity_start, LocalDateTime validity_end, LocalDateTime recharge_date) {
        this.subscriber = subscriber;
        this.plan = plan;
        this.amount = amount;
        this.payment_mode = payment_mode;
        this.validity_start = validity_start;
        this.validity_end = validity_end;
        this.recharge_date = recharge_date;
    }

    public PlanHistory(Subscriber subscriber, Plan plan, Payment payment, BigDecimal amount) {
        this.subscriber = subscriber;
        this.plan = plan;
        this.amount = amount.doubleValue();
        this.payment_mode = payment.getPayName() != null ? payment.getPayName() : "Unknown"; // Fallback if null
        System.out.println("Setting payment_mode: " + this.payment_mode); // Debug
        this.recharge_date = LocalDateTime.now();
        this.validity_start = LocalDateTime.now();
        try {
            int validityDays =  plan.getValidityDays();
            this.validity_end = LocalDateTime.now().plusDays(validityDays);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid validity days in Plan: " + plan.getValidityDays());
        }
    }

    // Getters and Setters
    public Long getRecharge_id() { return recharge_id; }
    public void setRecharge_id(Long recharge_id) { this.recharge_id = recharge_id; }
    public Subscriber getSubscriber() { return subscriber; }
    public void setSubscriber(Subscriber subscriber) { this.subscriber = subscriber; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPayment_mode() { return payment_mode; }
    public void setPayment_mode(String payment_mode) { this.payment_mode = payment_mode; }
    public LocalDateTime getValidity_start() { return validity_start; }
    public void setValidity_start(LocalDateTime validity_start) { this.validity_start = validity_start; }
    public LocalDateTime getValidity_end() { return validity_end; }
    public void setValidity_end(LocalDateTime validity_end) { this.validity_end = validity_end; }
    public LocalDateTime getRecharge_date() { return recharge_date; }
    public void setRecharge_date(LocalDateTime recharge_date) { this.recharge_date = recharge_date; }
}