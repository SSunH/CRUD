//package Sun.crud.config;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {
//
//    @Autowired
//    private HttpServletResponse response;
//
//    private void setJwtTokenCookie(String jwtToken) {
//        Cookie cookie = new Cookie("access_token", jwtToken);
//        cookie.setPath("/");
//        cookie.setMaxAge(3600); // 쿠키 만료 시간 (초 단위, 예: 1시간)
//        response.addCookie(cookie);
//    }
//
//    @Override
//    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//        String jwtToken = super.encode(accessToken, authentication);
//
//        // 여기에서 쿠키 설정을 수행
//        setJwtTokenCookie(jwtToken);
//
//        return jwtToken;
//    }
//}
