package com.tarek.OAuth2_Internal.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserController {

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal OidcUser oidcUser
            )
    {
       String email= oidcUser.getEmail();
       String name = oidcUser.getName();
       String subject =oidcUser.getSubject();
        return """
                naem: %s
                email: %s
                subject: %s
                """.formatted(name, email,subject);
    }

}
