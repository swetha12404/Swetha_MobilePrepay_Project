package com.example.productcrud.model;

import java.math.BigDecimal;

public class PaymentRequest {
    private String payName;
    private BigDecimal amount;
    private String paymentMode;
    private String upiId;
    private String cardHolder;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    private String bankName;
    private String pin;
    private Integer planId;         // Added for plan reference
    private String subscriberMobile; // Added for subscriber reference

    // Getters and Setters
    public String getPayName() { return payName; }
    public void setPayName(String payName) { this.payName = payName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
    public String getExpiryYear() { return expiryYear; }
    public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public String getSubscriberMobile() { return subscriberMobile; }
    public void setSubscriberMobile(String subscriberMobile) { this.subscriberMobile = subscriberMobile; }
}