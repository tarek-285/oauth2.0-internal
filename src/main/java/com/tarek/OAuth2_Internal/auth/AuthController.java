package com.tarek.OAuth2_Internal.auth;

import com.tarek.OAuth2_Internal.dto.ResendOtpRequest;
import com.tarek.OAuth2_Internal.dto.UserRegistrationDto;
import com.tarek.OAuth2_Internal.dto.VerifyEmailRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService)
    {
        this.authService=authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userRegistrationDto)
    {
        authService.registerUser(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message","Registration received. Check your email for verification code"
                )
        );
    }
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmailRequest(@RequestBody VerifyEmailRequest verifyEmailRequest)
    {
        authService.verifyEmailOtp(verifyEmailRequest);
        return  ResponseEntity.ok(
                Map.of("message","Email Verified successfully .You can login ")
        );

    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?>resendOtp(@RequestBody ResendOtpRequest resendOtpRequest)
    {
        authService.resendOtp(resendOtpRequest);
        return ResponseEntity.ok().body(
                Map.of("message", "Otp send successfully")
        );
    }



}
