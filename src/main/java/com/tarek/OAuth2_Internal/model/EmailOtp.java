package com.tarek.OAuth2_Internal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

@Table(name = "email_otp",
indexes = {
        @Index(name = "idx_email_otp",columnList = "user_id")
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EmailOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "code_hash")
    private String codeHash;

    private Instant createdAt;
    private Instant expiresAt;
    private Instant usedAt;
    private int attempt;

    public EmailOtp(User user, String codeHash, Instant expiresAt, Instant createdAt) {
           this.user= user;
           this.codeHash=codeHash;
           this.expiresAt=expiresAt;
           this. createdAt=createdAt;
    }

    public void markedUsed(Instant usedAt)
    {
        this.usedAt=usedAt;
    }
    public void incrementAttempts()
    {
        this.attempt++;

    }

}



