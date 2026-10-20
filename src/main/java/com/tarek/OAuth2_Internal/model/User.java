package com.tarek.OAuth2_Internal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String provider;
    private String providerSubject;

    public User(String email, String name, String provider, String providerSubject) {
        this.email=email;
        this.name=name;
        this.provider=provider;
        this.providerSubject=providerSubject;
    }
}
