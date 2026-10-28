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
import java.util.List;

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
    public void SendInitialOtp(User newuser)
    {

     otpHandler(newuser, false);
    }
    @Transactional
    public void resendOtp(String email)
    {
        var user =userRepository.findByEmailIgnoreCase(email).orElseThrow(()->
                new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "Incorrect email"));
        otpHandler(user,true);
    }
    @Transactional
    public void verifyOtp(String email , String submittedOtp)
    {

        var user = userRepository.findByEmailIgnoreCase(email).orElseThrow(this::invalidOtp);
//        if(user.isEmailVerified())
//        {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "This email already verified"
//            );
//
//        }

        var lastOtp = emailOtpRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).orElseThrow(this::invalidOtp);

        var record= emailOtpRepository.findByIdForUpdate(lastOtp.getId()).orElseThrow(this::invalidOtp);
        record.incrementAttempts();

        //EmailOtp record= lockedOtp;
        Instant now= Instant.now();

        if(record.getUsedAt()!=null)
        {

            throw new
                    ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                           "This verification code has already been used"
            );

        }
        if(!now.isBefore(record.getExpiresAt()))
        {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Verification code has expired. Please request a new one"
            );
        }
        if(record.getAttempt()>=5)
        {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                  " Maximum verification attempts exceeded. Please request a new code"
            );

        }


        if(!passwordEncoder.matches(submittedOtp,record.getCodeHash()))
        {
            emailOtpRepository.saveAndFlush(record);
         throw invalidOtp();
        }

        record.markedUsed(now);
        user.setEmailVerified(true);
        userRepository.save(user);
        emailOtpRepository.save(record);


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
//        emailOtpRepository.deleteByUserId(user.getId());
        String otp = "%06d".formatted(secureRandom.nextInt(1_000_000));
       EmailOtp newOtp= new EmailOtp(
                user,
                passwordEncoder.encode(otp),
                now.plus(expirationMinutes, ChronoUnit.MINUTES),
                now);
       emailOtpRepository.saveAndFlush(newOtp);
       emailService.sendOtp(user.getEmail(), otp);


    }

    private ResponseStatusException invalidOtp()
    {
        System.out.println("Invalid otp ");
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid varification code"
        );
    }

}
