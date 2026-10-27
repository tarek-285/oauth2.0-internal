package com.tarek.OAuth2_Internal.service;


import com.tarek.OAuth2_Internal.email.EmailService;
import com.tarek.OAuth2_Internal.email.SmptEmailService;
import com.tarek.OAuth2_Internal.model.EmailOtp;
import com.tarek.OAuth2_Internal.model.User;
import com.tarek.OAuth2_Internal.repository.EmailOtpRepository;
import com.tarek.OAuth2_Internal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class EmailOtpService {

    private final EmailService emailService;
    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom= new SecureRandom();

    @Value("${app.otp.expiration-minutes:5}")
    long  expirationMinutes;
    @Value("${app.otp.resend-cooldown-seconds:60}")
    private long resendCollDownSeconds;
    @Value("${app.otp.max-attempts:5}")
    private long maxAttempts;

    public EmailOtpService(EmailService emailService,
                           EmailOtpRepository emailOtpRepository,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository)
    {
        this.emailService=emailService;
        this.emailOtpRepository=emailOtpRepository;
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
    }

    @Transactional
    public void SendInitialOtp(User user)
    {
     otpHandler(user, false);
    }
    @Transactional
    public void resendOtp(String email)
    {
        User user = userRepository.findAllByEmailIgnoreCase(email).orElseThrow(
                ()-> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unable to process this request"
                )
        );
        if(user.isEmailVerified())
        {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already varified"
            );
        }
        otpHandler(user,true);

    }

    @Transactional
    public boolean verifyOtp(String email , String submittedOtp)
    {
        User user = userRepository.findAllByEmailIgnoreCase(email).orElseThrow(this::invalidOtp);
        if(user.isEmailVerified())
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This email already verified"
            );

        }
        var lastOtp = emailOtpRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
        if(lastOtp.isEmpty())
        {
            return false;
        }
        var lockedOtp= emailOtpRepository.findByIdForUpdate(lastOtp.get().getId());
        if(lockedOtp.isEmpty())
        {
            return false;

        }
        EmailOtp record= lockedOtp.get();
        Instant now= Instant.now();
        if(record.getUsedAt()!=null
                || !now.isBefore(record.getExpiresAt())
                || record.getAttempt()>=maxAttempts)
        {
            return false;
        }
        if(!passwordEncoder.matches(record.getCodeHash(), submittedOtp))
        {
            record.incrementAttempts();
            emailOtpRepository.saveAndFlush(record);
            return false;
        }
        record.markedUsed(now);
        user.setEmailVerified(true);
        userRepository.save(user);
        emailOtpRepository.save(record);
        return true;

    }

    public void otpHandler(User user , boolean enforceCoolDown )
    {
        Instant now =Instant.now();
        var prev=emailOtpRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
        if(enforceCoolDown && prev.isPresent())
        {
            Instant wait= prev.get().getCreatedAt().plusSeconds(resendCollDownSeconds);
            if(now.isBefore(wait))
            {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Please wait before requesting another code"
                );
            }

        }
        emailOtpRepository.deleteByUserId(user.getId());
        String otp = "%06d".formatted(secureRandom.nextInt(1_000_000));
       EmailOtp newOtp= new EmailOtp(user,
                passwordEncoder.encode(otp),
                now.plus(expirationMinutes, ChronoUnit.MINUTES),
                now);
       emailOtpRepository.saveAndFlush(newOtp);
       emailService.sendOtp(user.getEmail(), otp);


    }

    private ResponseStatusException invalidOtp()
    {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid or expired varification code"
        );
    }

}
