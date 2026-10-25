package com.tarek.OAuth2_Internal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String name;
    private String email;
    private String password;
}
