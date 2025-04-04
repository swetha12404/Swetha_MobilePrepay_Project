package com.example.productcrud.model;

public class PaymentResponse {
    private boolean success;
    private String message;
    private TransactionHistory transaction;
    private String redirectUrl;

    public PaymentResponse() {}

    public PaymentResponse(boolean success, String message, TransactionHistory transaction, String redirectUrl) {
        this.success = success;
        this.message = message;
        this.transaction = transaction;
        this.redirectUrl = redirectUrl;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public TransactionHistory getTransaction() { return transaction; }
    public void setTransaction(TransactionHistory transaction) { this.transaction = transaction; }
    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
}