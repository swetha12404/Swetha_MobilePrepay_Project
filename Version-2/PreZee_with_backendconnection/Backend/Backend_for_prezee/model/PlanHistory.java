package com.example.productcrud.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Plan_History")
public class PlanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recharge_id")
    private Integer rechargeId;

    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "recharge_date", nullable = false, updatable = false)
    private LocalDateTime rechargeDate = LocalDateTime.now();

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "validity_start", nullable = false)
    private LocalDate validityStart = LocalDate.now();

    @Column(name = "validity_end", nullable = false)
    private LocalDate validityEnd;

    // Default constructor
    public PlanHistory() {}

    // Constructor to calculate validityEnd based on plan's validityDays
    public PlanHistory(Subscriber subscriber, Plan plan, Payment payment, BigDecimal amount) {
        this.subscriber = subscriber;
        this.plan = plan;
        this.payment = payment;
        this.amount = amount;
        this.rechargeDate = LocalDateTime.now();
        this.validityStart = LocalDate.now();
        this.validityEnd = calculateValidityEnd(plan.getValidityDays());
    }

    // Method to calculate validityEnd based on validityDays
    private LocalDate calculateValidityEnd(String validityDays) {
        int days = Integer.parseInt(validityDays); // Assuming validityDays is a numeric string
        return validityStart.plusDays(days);
    }

    // Getters and Setters
    public Integer getRechargeId() { return rechargeId; }
    public void setRechargeId(Integer rechargeId) { this.rechargeId = rechargeId; }
    public Subscriber getSubscriber() { return subscriber; }
    public void setSubscriber(Subscriber subscriber) { this.subscriber = subscriber; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public LocalDateTime getRechargeDate() { return rechargeDate; }
    public void setRechargeDate(LocalDateTime rechargeDate) { this.rechargeDate = rechargeDate; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getValidityStart() { return validityStart; }
    public void setValidityStart(LocalDate validityStart) { this.validityStart = validityStart; }
    public LocalDate getValidityEnd() { return validityEnd; }
    public void setValidityEnd(LocalDate validityEnd) { this.validityEnd = validityEnd; }
}