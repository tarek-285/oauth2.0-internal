package com.tarek.OAuth2_Internal.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/login")
    public String loing()
    {
        return "Login : http://localhost:8080/oauth2/authorization/google";
    }
}


