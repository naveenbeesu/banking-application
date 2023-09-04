package com.xyz.bankingapi.service.account;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private final Map<String, String> otpStore = new HashMap<>();
    private final Random random = new Random();
    public String sendOtp(String mobileNumber) {
        String otp = generateOtp();
        System.out.println("Sending OTP to " + mobileNumber + ": " + otp);
        otpStore.put(mobileNumber, otp);
        return otp;
    }
    public boolean validateOtp(String mobileNumber, String enteredOtp) {
        String existingOtp = otpStore.get(mobileNumber);
        return existingOtp != null && existingOtp.equals(enteredOtp);
    }
    private String generateOtp() {
        int otpValue = 100000 + random.nextInt(900000); // Generates a 6-digit OTP
        return String.valueOf(otpValue);
    }
}