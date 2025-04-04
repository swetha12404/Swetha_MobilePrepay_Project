package com.example.productcrud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPaymentConfirmationEmail(String toEmail, String subscriberName, String planName, double amountPaid) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Payment Confirmation - PreZee");
        message.setText(String.format(
            "Dear %s,\n\n" +
            "We are pleased to inform you that your payment has been successfully processed.\n\n" +
            "Payment Details:\n" +
            "Plan Name: %s\n" +
            "Amount Paid: ₹%.2f\n" +
            "Transaction Date: %s\n\n" +
            "Thank you for choosing PreZee!\n\n" +
            "Best regards,\n" +
            "The PreZee Team",
            subscriberName, planName, amountPaid, LocalDateTime.now().toString()
        ));
        mailSender.send(message);
    }

    public void sendNotificationEmail(String toEmail, String subscriberName, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Notification from PreZee");
        mailMessage.setText(String.format(
            "Dear %s,\n\n" +
            "%s\n\n" +
            "Best regards,\n" +
            "The PreZee Team",
            subscriberName, message
        ));
        mailSender.send(mailMessage);
    }

    public void sendTestEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Test Email - PreZee");
        message.setText("This is a test email from PreZee to verify email functionality.\n\n" +
                        "Best regards,\n" +
                        "The PreZee Team");
        mailSender.send(message);
    }
}