package com.tarek.OAuth2_Internal.auth;

import com.tarek.OAuth2_Internal.dto.ResendOtpRequest;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public void registerUser(UserRegistrationDto registrationDto)
    {
        String email = registrationDto.getEmail().trim().toLowerCase(Locale.ROOT);
        List<User> usrList=userRepository.findAllByEmailIgnoreCase(email);
        User verifiedUser =null;
        for(User user : usrList)
        {
            if(!user.isEmailVerified())
            {
                userRepository.delete(user);
            }else
            {
                verifiedUser=user;
            }
        }
        if(verifiedUser!=null)
        {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Try another email "
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
        emailOtpService.verifyOtp(emailRequest.email(),emailRequest.otp());

    }

    public void resendOtp(ResendOtpRequest resendOtpRequest) {
        emailOtpService.resendOtp(resendOtpRequest.email());
    }
}
