package Sun.crud.controller;

import kong.unirest.Unirest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Sun.crud.entity.OauthToken;

@RestController
public class OAuthController {

    // 클라이언트가 구현해야하는 코드 - 발급 받은 코드로 토큰 발행
    @RequestMapping("/callback")
    public String code(@RequestParam String code){

        String cridentials = "clientId:secretKey";
        // base 64로 인코딩 (basic auth 의 경우 base64로 인코딩 하여 보내야한다.)
        String encodingCredentials = new String(
                Base64.encodeBase64(cridentials.getBytes())
        );
        String requestCode = code;
        OauthToken.request.accessToken request = new OauthToken.request.accessToken(){{
            setCode(requestCode);
            setGrant_type("authorization_code");
            setRedirect_uri("http://localhost:8080/callback");
        }};
        
        try {
            // oauth 서버에 http 통신으로 토큰 발행
            OauthToken.response oauthToken = Unirest.post("http://localhost:8080/oauth/token")
                    .header("Authorization","Basic "+encodingCredentials)
                    .fields(request.getMapData())
                    .asObject(OauthToken.response.class).getBody();
            System.err.println("토큰생성 완료");
            System.err.println(oauthToken);
            // 토큰 발급 성공 시 "로그인 성공" 응답
            return "로그인 성공";
        } catch (Exception e) {
            // 토큰 발급 실패 시 에러 메시지를 반환
            e.printStackTrace();
            return "토큰 발급 실패";
        }
    }
}
