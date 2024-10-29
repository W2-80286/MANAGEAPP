package com.agri.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OtpService {
    public String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(999999); // Generate a 4-digit OTP
        return String.valueOf(otp);
    }

    private Map<String, String> otpStorage = new HashMap<>();

    // Generate a 4-digit OTP
    public String generateOtp(String mobileNumber) {
        String otp = String.valueOf(new Random().nextInt(9000) + 1000); // Generates a 4-digit OTP
        otpStorage.put(mobileNumber, otp);

        // Simulate sending OTP (in a real app, you would integrate with an SMS API)
        System.out.println("OTP for " + mobileNumber + ": " + otp);

        return otp;
    }

    // Validate OTP
    public boolean validateOtp(String mobileNumber, String otp) {
        return otp.equals(otpStorage.get(mobileNumber));
    }

    // Clear OTP after validation
    public void clearOtp(String mobileNumber) {
        otpStorage.remove(mobileNumber);
    }

    }
