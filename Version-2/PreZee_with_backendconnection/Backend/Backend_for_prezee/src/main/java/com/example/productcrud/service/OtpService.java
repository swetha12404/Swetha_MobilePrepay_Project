package com.example.productcrud.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.productcrud.config.TwilioConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private TwilioConfig twilioConfig;

    private final Map<String, String> otpStorage = new HashMap<>(); // Temporary storage for OTPs

    public String generateAndSendOtp(String mobile) {
        String otp = String.format("%04d", new Random().nextInt(10000)); // Generate 4-digit OTP
        otpStorage.put(mobile, otp); // Store OTP temporarily

        // Send OTP via Twilio
        String formattedMobile = "+91" + mobile; // Assuming Indian numbers; adjust as needed
        Message.creator(
                new PhoneNumber(formattedMobile),
                new PhoneNumber(twilioConfig.getTwilioPhoneNumber()),
                "Your OTP for PreZee profile validation is: " + otp
        ).create();

        return otp; // For debugging; remove in production
    }

    public boolean verifyOtp(String mobile, String otp) {
        String storedOtp = otpStorage.get(mobile);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(mobile); // Remove OTP after verification
            return true;
        }
        return false;
    }
}