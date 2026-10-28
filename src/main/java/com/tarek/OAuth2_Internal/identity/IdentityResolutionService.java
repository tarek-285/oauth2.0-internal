package com.tarek.OAuth2_Internal.identity;
import com.tarek.OAuth2_Internal.model.User;
import com.tarek.OAuth2_Internal.repository.UserIdentityRepository;
import com.tarek.OAuth2_Internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
@Slf4j
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

        //UserIdentity present return the user
        //No userIdentity
        //verified useridentity and verified application account
        //

        var existingIdentity =
                identityRepository.findByProviderAndProviderSubject(
                        provider, providerSubject
                );


        if (existingIdentity.isPresent()) {
            User user = existingIdentity.get().getUser();
           if(user.isEmailVerified() && !providerEmailVerified)
           {
               throw new ResponseStatusException(
                       HttpStatus.CONFLICT,
                       "Account is verified, but incoming provider email is unverified."


               );

           }
           //update property
            user.setEmail(
                    StringUtils.hasText(email)
                            ? email.trim().toLowerCase(Locale.ROOT)
                            : provider+"_"+providerSubject
            );
            user.setDisplayName(name);
            user.setAvatarUrl(picture);
            user.setEmailVerified(providerEmailVerified);
            return user;
        }


        User user = null;

        if (StringUtils.hasText(email)) {
            email = email.trim()
                    .toLowerCase(Locale.ROOT);
        }
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

       //one verified user already exit ,
       //retun error for unverified provider cannot create a accout with this email
         if(user!= null && !providerEmailVerified)
         {
             throw new ResponseStatusException(
                     HttpStatus.CONFLICT,
                     "An account with this email already exists. Verify your email with " + provider +" before linking."
             );
         }






        if (user == null) {
            user = new User();
            user.setDisplayName(name);
            user.setEmail(
                    StringUtils.hasText(email)
                            ? email.trim().toLowerCase(Locale.ROOT)
                            : provider+"_"+providerSubject
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

