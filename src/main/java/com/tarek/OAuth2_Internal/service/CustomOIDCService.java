package com.tarek.OAuth2_Internal.service;

import com.tarek.OAuth2_Internal.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;


public class CustomOIDCService implements OAuth2UserService<OidcUserRequest, OidcUser> {


    private final UserService userService;
    OidcUserService oidcUserService =new OidcUserService();
    public CustomOIDCService(UserService userService) {
        System.out.println("I am customOIDC service goit invokeddd");
        this.userService=userService;
    }


    @Override
    public @Nullable OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

      OidcUser oidcUser=  oidcUserService.loadUser(userRequest);

      String provider =userRequest.getClientRegistration().getRegistrationId();
      System.out.println(userRequest.getClientRegistration().getClientName());

        System.out.println(" i am invokedd");

      //userService.registerOrUpdate(provider, oidcUser);

      return oidcUser;
    }
}
