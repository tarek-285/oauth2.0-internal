package com.tarek.OAuth2_Internal.identity;


import java.util.Locale;

public enum AuthProvider {
    GOOGLE("google"),
    GITHUB("github"),
    FACEBOOK("facebook"),
    GITLAB("gitlab");

    private final String registrationId;
    AuthProvider(String registrationId){
        this.registrationId=registrationId;
    }

    public static AuthProvider findProvider(String registrationId)
    {
        for(AuthProvider provider : values())
        {
          if(provider.registrationId.equalsIgnoreCase(registrationId))
            {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unsupported provider for "+registrationId);
    }



}