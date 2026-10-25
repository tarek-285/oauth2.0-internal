
package com.tarek.OAuth2_Internal.auth;


import com.tarek.OAuth2_Internal.dto.AuthResponse;
import com.tarek.OAuth2_Internal.identity.AuthProvider;
import com.tarek.OAuth2_Internal.identity.IdentityResolutionService;
import com.tarek.OAuth2_Internal.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final IdentityResolutionService identityService;
    private final ObjectMapper objectMapper;
    private final AuthUtils authUtils;


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        OAuth2User oauthUser = (OAuth2User) oauthToken.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        AuthProvider provider = AuthProvider.findProvider(registrationId);
        String subject = authUtils.getSubject(attributes, oauthUser);
        String email = authUtils.findKeyValue(attributes, "email");
        String name = authUtils.extractNameByProvider(provider, attributes);
        String picture = null;
                //authUtils.extractProfilePicture(attributes);
        boolean providerEmailVerified = authUtils.isEmailVerified(attributes, oauthUser);

        User user = identityService.resolve(
                provider,
                subject,
                email,
                name,
                picture,
                providerEmailVerified
        );


        if (!"ACTIVE".equals(user.getStatus())) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "This account is not active"
            );
            return;
        }

        String accessToken = "flhgdgfl;gal;sdf";

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                "Bearer",
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getRole(),
                user.isEmailVerified()
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        objectMapper.writeValue(response.getWriter(), authResponse);
    }

}