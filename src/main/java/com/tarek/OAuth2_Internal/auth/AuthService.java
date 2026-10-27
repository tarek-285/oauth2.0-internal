package com.tarek.OAuth2_Internal.auth;

import com.tarek.OAuth2_Internal.dto.UserRegistrationDto;
import com.tarek.OAuth2_Internal.dto.VerifyEmailRequest;
import com.tarek.OAuth2_Internal.model.User;
import com.tarek.OAuth2_Internal.repository.UserRepository;
import com.tarek.OAuth2_Internal.service.EmailOtpService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;


@Service
public class AuthService {
     private final UserRepository userRepository;
     private final EmailOtpService emailOtpService;
     private final PasswordEncoder passwordEncoder;

     public AuthService(UserRepository userRepository,
                        EmailOtpService emailOtpService,
                        PasswordEncoder passwordEncoder)
     {
         this.emailOtpService=emailOtpService;
         this.passwordEncoder=passwordEncoder;
         this.userRepository=userRepository;
     }

    @Transactional
    public void registerUser(UserRegistrationDto registrationDto)
    {
        String email = registrationDto.getEmail().trim().toLowerCase(Locale.ROOT);
       if(userRepository.existsByEmailIgnoreCase(email))
       {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to register this email address"
            );
       }
       User user = new User();
       user.setDisplayName(registrationDto.getName());
       user.setEmail(registrationDto.getEmail());
       user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
       User savedUser= userRepository.save(user);
       emailOtpService.SendInitialOtp(user);
    }
    public void verifyEmailOtp(VerifyEmailRequest emailRequest)
    {
       boolean isSaved= emailOtpService.verifyOtp(emailRequest.email(),emailRequest.otp());
       if(!isSaved)
       {
           throw new ResponseStatusException(
                   HttpStatus.BAD_REQUEST,
                   "Incorrect otp !"+emailRequest.otp()
           );
       }
    }
}
