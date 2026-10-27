package com.tarek.OAuth2_Internal.dto;

public record VerifyEmailRequest(

         String email,
         String otp
) {
}
