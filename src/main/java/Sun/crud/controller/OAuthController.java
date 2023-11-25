package Sun.crud.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import kong.unirest.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@RestController
public class OAuthController {

    private final RestTemplate restTemplate;

    public OAuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }  
    
    @RequestMapping("/callback")
    private ResponseEntity<String> requestToken(String code) {
        String credentials = "clientId:secretKey";
        String encodingCredentials = new String(Base64.getEncoder().encode(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodingCredentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", "http://localhost:8080/callback");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);

        return restTemplate.exchange(
                "http://localhost:8080/oauth/token",
                HttpMethod.POST,
                tokenRequest,
                String.class
        );
    }
    
    
    @RequestMapping("/ready")
    public String processTokenResponse(@RequestParam String accessToken) {
        try {
//            // 확인: 토큰 요청 응답에서 JSON 형식의 문자열을 가져옴
//        	String responseBody = tokenResponse.getBody();
//           
//
//            // JSON 문자열을 JSONObject로 파싱
//            JSONObject jsonToken = new JSONObject(responseBody);
//
//            // 여기서부터는 필요한 작업 수행
//            String accessToken = jsonToken.getString("access_token");

            // 리소스 서버에 요청
            ResponseEntity<String> resourceResponse = restTemplate.exchange(
                    "http://localhost:9090/main",
                    HttpMethod.GET,
                    createResourceRequest(accessToken),
                    String.class
            );

            // 확인: 리소스 서버 응답에서 JSON 형식의 문자열을 가져옴
            String resourceBody = resourceResponse.getBody();
            System.out.println("Resource Server Response Body: " + resourceBody);
            // 리소스 서버 응답 로깅

            // 리소스 서버 응답을 클라이언트에게 반환
            return "리소스 서버 응답: " + resourceBody;
        } catch (Exception e) {
            e.printStackTrace();
            return "토큰 응답 처리 또는 리소스 서버 요청 실패: " + e.getMessage();
        }
    }

    private HttpEntity<Void> createResourceRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        System.err.println("Resource Request Headers: " + headers);
        return new HttpEntity<>(headers);
    }    
    
   
//    @RestController
//    public class OAuthController {
//        private String ACCESS_TOKEN_COOKIE_NAME = "hello";
//
//        @RequestMapping("/callback")
//        public String code(@RequestParam String code, HttpServletResponse response) {
//            String cridentials = "clientId:secretKey";
//            String encodingCredentials = new String(Base64.getEncoder().encode(cridentials.getBytes()));
//            String requestCode = code;
//
//            try {
//                HttpResponse<String> tokenResponse = Unirest.post("http://localhost:8080/oauth/token")
//                        .header("Authorization", "Basic " + encodingCredentials)
//                        .field("code", requestCode)
//                        .field("grant_type", "authorization_code")
//                        .field("redirect_uri", "http://localhost:8080/callback")
//                        .asString();
//
//                // 확인: 토큰 요청 응답에서 JSON 형식의 문자열을 가져옴
//                String responseBody = tokenResponse.getBody();
//                System.out.println("Token Response Body: " + responseBody);
//
//                // JSON 문자열을 JSONObject로 파싱
//                JSONObject jsonToken = new JSONObject(responseBody);
//
//                // 여기서부터는 필요한 작업 수행
//                String accessToken = jsonToken.getString("access_token");
//
////                // 쿠키에 토큰을 저장
////                Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
////                accessTokenCookie.setMaxAge((int) jsonToken.getLong("expires_in"));
////                accessTokenCookie.setPath("/");
////                response.addCookie(accessTokenCookie);
//                
//             // 엑세스 토큰을 HTTP 헤더에 실어 보내 리소스 서버에 요청
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Accept", "application/json");
//                headers.put("Authorization", "Bearer " + accessToken);            
//                HttpResponse<String> resourceResponse = Unirest.put("http://localhost:9090/main")
//                        .headers(headers)
//                        .asString();
//                
//                System.err.println(accessToken);
//                
//
//                // 리소스 서버 응답을 클라이언트에게 반환
//                return "리소스 서버 응답: " + resourceResponse.getBody();
//
//                // 성공적으로 처리되었음을 클라이언트에게 알림
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "토큰 발급 및 리소스 서버 요청 실패: " + e.getMessage();
//            }
//        }
//    }
    //분리해야함.


    
    
}


