package com.tarek.OAuth2_Internal.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmptEmailService implements EmailService{

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    public SmptEmailService(JavaMailSender javaMailSender)
    {
        this.javaMailSender=javaMailSender;
    }
    @Override
    public void sendOtp(String to, String code) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setSubject("Verify your email address");
        simpleMailMessage.setText("""
                Hello,
                your email verification code is: %s
                This code expires in 5 minutes.
                If you did not request this, you can ignore this
                """.formatted(code));
      try {
          javaMailSender.send(simpleMailMessage);
      }catch (Exception e)
      {
          throw  new RuntimeException(e.getMessage());
      }

    }


}
