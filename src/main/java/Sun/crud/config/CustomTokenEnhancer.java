package Sun.crud.config;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import Sun.crud.entity.SignupEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// custom jwt token add info
public class CustomTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        try {
            Object principal = authentication.getPrincipal();

            if (principal instanceof SignupEntity) {
                SignupEntity signupEntity = (SignupEntity) principal;
                
                Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("name", signupEntity.getName());
                additionalInfo.put("email", signupEntity.getEmail());
                additionalInfo.put("role", signupEntity.getRole());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

            } else {
            }
            return accessToken;
        } catch (Exception e) {
            throw e;
        }
    }
}
