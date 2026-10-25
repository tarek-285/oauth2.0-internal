package com.tarek.OAuth2_Internal.repository;

import com.tarek.OAuth2_Internal.identity.AuthProvider;
import com.tarek.OAuth2_Internal.identity.UserIdentity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserIdentityRepository
        extends JpaRepository<UserIdentity, Long> {


    @EntityGraph(attributePaths = {"user"})
    Optional<UserIdentity> findByProviderAndProviderSubject(
            AuthProvider provider,
            String providerSubject
    );

    boolean existsByProviderAndProviderSubject(
            AuthProvider provider,
            String providerSubject
    );
}
