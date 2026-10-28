package com.tarek.OAuth2_Internal.email;

import com.tarek.OAuth2_Internal.model.EmailOtp;
import com.tarek.OAuth2_Internal.repository.EmailOtpRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttemptsOtp {
    private final EmailOtpRepository emailOtpRepository;
    public AttemptsOtp(EmailOtpRepository emailOtpRepository)
    {
        this.emailOtpRepository=emailOtpRepository;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int attemptsIncrement(Long id,Long maxAttempts)
    {

      return emailOtpRepository.incrementAttempts(id, maxAttempts);


    }


}
