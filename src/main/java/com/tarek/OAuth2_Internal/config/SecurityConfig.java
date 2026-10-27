package com.tarek.OAuth2_Internal.config;

import com.tarek.OAuth2_Internal.auth.OAuth2LoginFailureHandler;
import com.tarek.OAuth2_Internal.auth.OAuth2LoginSuccessHandler;
import com.tarek.OAuth2_Internal.service.CustomOIDCService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   OAuth2LoginSuccessHandler successHandler,
                                                   OAuth2LoginFailureHandler failureHandler
                                                   ) throws Exception{

        httpSecurity
                .csrf(c->c.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/**",
                                "/error",
                                "/api/auth/**"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .oauth2Login(oauth2-> oauth2
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)


                );


        return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
