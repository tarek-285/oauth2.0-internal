package com.tarek.OAuth2_Internal.repository;

import com.tarek.OAuth2_Internal.model.EmailOtp;
import com.tarek.OAuth2_Internal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

     List<User> findAllByEmailIgnoreCase(String email);
     Optional<User> findByEmailIgnoreCase(String email);

     boolean existsByEmail(String email);
     boolean existsByEmailIgnoreCase(String email);


}
