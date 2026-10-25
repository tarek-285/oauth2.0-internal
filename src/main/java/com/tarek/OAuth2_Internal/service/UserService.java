package com.tarek.OAuth2_Internal.service;

import com.tarek.OAuth2_Internal.dto.UserRegistrationDto;
import com.tarek.OAuth2_Internal.model.User;
import com.tarek.OAuth2_Internal.repository.UserIdentityRepository;
import com.tarek.OAuth2_Internal.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserIdentityRepository userIdentityRepository)
    {
        this.userRepository=userRepository;
        this.userIdentityRepository=userIdentityRepository;
        this.passwordEncoder=passwordEncoder;

    }





}
