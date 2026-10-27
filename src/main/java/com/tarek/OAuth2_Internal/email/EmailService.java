package com.tarek.OAuth2_Internal.email;

public interface EmailService {
    void sendOtp(String to ,String code);
}
