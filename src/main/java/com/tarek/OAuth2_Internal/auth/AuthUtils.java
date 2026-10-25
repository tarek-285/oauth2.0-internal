package com.tarek.OAuth2_Internal.auth;

import com.tarek.OAuth2_Internal.identity.AuthProvider;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthUtils {


  public String getSubject(Map<String ,Object>attributes,OAuth2User user){

      if(user instanceof OidcUser oidcUser)
      {
         return oidcUser.getSubject();
      }
      String id = findKeyValue(attributes,"id");
      if(id==null)
      {
          id= findKeyValue(attributes,"sub");
      }
      if(id ==null)
      {
          throw new IllegalArgumentException("Provider did not return a stable ID");
      }
      return id;
  }

  public boolean isEmailVerified(Map<String , Object> attributes ,OAuth2User user)
  {
      if(user instanceof OidcUser oidcUser)
      {
        return Boolean.TRUE.equals( oidcUser.getEmailVerified());
      }

      Object verified= attributes.get("email_verified");
      return Boolean.TRUE.equals(verified);

  }
  public String extractProfilePicture(Map<String,Object>attributes)
  {
      String url= findKeyValue(attributes,"picture");
      if(url==null)
      {
          url= findKeyValue(attributes,"avatar_url");

      }
      return url;
  }
    private String extractPicture(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GITHUB -> findKeyValue(attributes, "avatar_url");
            case GOOGLE -> findKeyValue(attributes, "picture");
            case FACEBOOK -> {
                if (attributes.get("picture") instanceof Map<?, ?> pictureMap &&
                        pictureMap.get("data") instanceof Map<?, ?> dataMap) {
                    Object url = dataMap.get("url");
                    yield (url != null) ? url.toString() : null;
                }
                yield null;
            }
            case GITLAB -> {
                yield findKeyValue(attributes, "picture");
            }
        };
    }

  public String  extractNameByProvider(AuthProvider authProvider,Map<String,Object>attributes)
  {
      return switch (authProvider)
      {
          case GITHUB ->{
              String name= findKeyValue(attributes,"name");
              yield name==null? null:findKeyValue(attributes, "login");
          }
          case GOOGLE,FACEBOOK -> findKeyValue(attributes,"name");
          case GITLAB -> findKeyValue(attributes,"user_name");

      };

  }

public String findKeyValue(Map<String ,Object> attributes, String key)
{

    Object value= attributes.get(key);
    if(value ==null)
    {
        return null;
    }

    String result= value.toString().trim();
    return result.isEmpty()?null:result;
}


}
