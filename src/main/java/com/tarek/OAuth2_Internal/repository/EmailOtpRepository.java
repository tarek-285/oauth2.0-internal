package com.tarek.OAuth2_Internal.repository;

import com.tarek.OAuth2_Internal.model.EmailOtp;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT o FROM EmailOtp o
        WHERE o.id = :id
        """)
    Optional<EmailOtp> findByIdForUpdate(@Param("id") Long id);

//    @Modifying
//    @Query("UPDATE EmailOtp e SET e.attempt = e.attempt + 1 WHERE e.id = :id")
//    void incrementAttemptsAtomically(@Param("id") Long id);

       @Modifying
       @Query("""
       UPDATE EmailOtp e set e.attempt= e.attempt+1 where e.id=:id AND e.attempt<=:maxAttempts
""")
       int incrementAttempts(@Param("id") Long id, @Param("maxAttempts") Long max);

    Optional<EmailOtp> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}
