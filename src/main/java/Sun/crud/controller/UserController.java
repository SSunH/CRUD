package Sun.crud.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import Sun.crud.DTO.LoginDTO;
import Sun.crud.entity.SignupEntity;
import Sun.crud.service.LoginService;
import Sun.crud.utils.JwtUtil;


@Controller
public class UserController {
	@Autowired
	private LoginService loginService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	 @GetMapping("/loginok")
	    public String login(){
	        return "login";
	    }
	 
	 @PostMapping("/loginok")
	 public ResponseEntity<String> loginPost(HttpServletRequest request, HttpServletResponse response, @RequestBody SignupEntity member) {
		    System.out.println("로그인 요청 받음: " + member);
		    String id= member.getId();
		    	     // 기존 토큰 생성 로직
	     Map<String, String> tokens = generateTokens(member);
	     System.err.println(member);

	     // 기존 HTTPONLY 쿠키 설정 로직
	     HttpHeaders headers = setRefreshCookie(tokens.get("refresh_token"));

	     // 기존 액세스 토큰을 LoginDTO에 설정하는 로직
	     LoginDTO loginDTO = new LoginDTO();
	     String accessToken = tokens.get("access_token");
	     loginDTO.setAccess_token("Bearer " + accessToken);

	     // 로그인 확인 로직
	     SignupEntity signupEntity = loginService.checklogin(member.getId(), member.getPassword());
	     System.err.println(signupEntity);	
	     if (signupEntity != null) {
	         // 업데이트된 헤더와 함께 성공 상태의 ResponseEntity 반환
	         ResponseEntity<String> responseEntity = ResponseEntity.status(200).headers(headers).body(loginDTO.getAccess_token());

	         // 새로운 서버로 토큰 전송
	         sendTokenToOtherServer(accessToken);

	         return responseEntity;
	     } else {
	         // 로그인 실패 처리
	         return ResponseEntity.status(401).body("로그인 실패");
	     }
	 }
	 
	 private void sendTokenToOtherServer(String accessToken) {
		    // 다른 서버의 엔드포인트
		    String otherServerUrl = "http://localhost:9090/main";

		    // HTTP 요청 헤더 설정
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);

		    // HTTP 요청 바디 설정
		    Map<String, String> requestBody = new HashMap<>();
		    requestBody.put("access_token", accessToken);

		    // HTTP 요청 엔티티 생성
		    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

		    // RestTemplate을 사용한 HTTP POST 요청
		    new RestTemplate().postForEntity(otherServerUrl, requestEntity, String.class);
		}
	 
	 
	 private Map<String, String> generateTokens(SignupEntity member) {
	        // jti 생성
	        String jti = JwtUtil.generateJti();
	        // 액세스 토큰 생성
	        String access_token = JwtUtil.generateAccessToken(member.getId(), member.getName(), member.getRole(), jti);
	        // 리프레시 토큰 생성
	        String refresh_token = JwtUtil.generateRefreshToken(member.getId(), member.getName(), member.getRole(),  jti);

	        // 토큰들을 맵에 저장
	        Map<String, String> tokens = new HashMap<>();
	        tokens.put("access_token", access_token);
	        tokens.put("refresh_token", refresh_token);
	        return tokens;
	    }
	 
	 private HttpHeaders setRefreshCookie(String refreshToken) {
	        // HTTPONLY 쿠키 생성
	        ResponseCookie HTTP_refresh_token = loginService.setRefeshCookie(refreshToken);
	        HttpHeaders headers = new HttpHeaders();
	        // 쿠키를 헤더에 추가
	        headers.add("Set-Cookie", HTTP_refresh_token.toString());
	        return headers;
	    }
	

}

