package com.tarek.OAuth2_Internal.identity;
import com.tarek.OAuth2_Internal.model.User;
import com.tarek.OAuth2_Internal.repository.UserIdentityRepository;
import com.tarek.OAuth2_Internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdentityResolutionService {

    private final UserRepository userRepository;
    private final UserIdentityRepository identityRepository;

    @Transactional
    public User resolve(
            AuthProvider provider,
            String providerSubject,
            String email,
            String name,
            String picture,
            boolean providerEmailVerified
    ) {
        if (!StringUtils.hasText(providerSubject)) {
            throw new IllegalArgumentException(
                    "Provider subject is missing"
            );
        }

        var existingIdentity =
                identityRepository.findByProviderAndProviderSubject(
                        provider, providerSubject
                );


        if (existingIdentity.isPresent()) {
            User user = existingIdentity.get().getUser();

            return user;
        }


        User user = null;

        if (StringUtils.hasText(email)) {
            String normalizedEmail = email.trim()
                    .toLowerCase(Locale.ROOT);
    var applicationUser= userRepository.findAllByEmailIgnoreCase(email);
       for(User isPresent: applicationUser)
            {
                if(!isPresent.isEmailVerified())
                {

                    userRepository.delete(isPresent);

                }else {
                    user = isPresent;
                }
            }

          userRepository.flush();

       


        }


        if (user == null) {
            user = new User();
            user.setDisplayName(name);
            user.setEmail(
                    StringUtils.hasText(email)
                            ? email.trim().toLowerCase(Locale.ROOT)
                            : null
            );
            user.setAvatarUrl(picture);


            user.setEmailVerified(providerEmailVerified);
            user.setRole("USER");
            user.setStatus("ACTIVE");

            user = userRepository.save(user);
        }

        // 4. Link this external identity to the resolved user.
        UserIdentity identity = new UserIdentity();
        identity.setProvider(provider);
        identity.setProviderSubject(providerSubject);
        identity.setProviderEmail(email);
        identity.setUser(user);

        identityRepository.save(identity);

        System.out.println(user.toString());
        return user;
    }
}

