package com.tarek.OAuth2_Internal.service;

import com.tarek.OAuth2_Internal.model.User;
import com.tarek.OAuth2_Internal.repository.UserRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository)
    {
        this.userRepository=userRepository;
    }

    public User registerOrUpdate(String provider, OidcUser oidcUser)
    {
        String providerSubject = oidcUser.getSubject();

      Optional<User> isExistUser =userRepository.findByProviderAndProviderSubject(provider, providerSubject);

      String name =oidcUser.getAttribute("name");
      String email=oidcUser.getEmail();
      User user;
      if(isExistUser.isPresent())
      {
          user = isExistUser.get();
         user.setName(name);
         user.setEmail(email);
         userRepository.save(user);
         return user;
      }
      user = new User(email,name, provider,providerSubject);
      userRepository.save(user);

      return user;

    }
}
