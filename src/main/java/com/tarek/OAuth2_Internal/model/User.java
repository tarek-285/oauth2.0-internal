package com.tarek.OAuth2_Internal.model;
import com.tarek.OAuth2_Internal.identity.UserIdentity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        }
)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name", length = 150)
    private String displayName;

    @Column(length = 320)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;


    private String password;

    @Column(name = "avatar_url", length =5000)
    private String avatarUrl;

    @Column(nullable = false, length = 30)
    private String role = "USER";

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<UserIdentity> identities = new ArrayList<>();
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EmailOtp> emailOtps=new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
