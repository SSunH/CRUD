package Sun.crud.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import Sun.crud.DTO.LoginDTO;
import Sun.crud.DTO.UserDTO;
import Sun.crud.entity.RefreshToken;
import Sun.crud.entity.SignupEntity;
import Sun.crud.service.LoginService;
import Sun.crud.service.refreshTokenService;
import Sun.crud.utils.JwtUtil;



@Controller
public class UserController {
	@Autowired
	private LoginService loginService;
	@Autowired
	private refreshTokenService refreshTokenService;	
	 @GetMapping("/login")
	    public String login(){
	        return "login";
	    }	 
	 @PostMapping("/login")
	 public ResponseEntity<String> loginPost(HttpServletRequest request, HttpServletResponse response,
	         @RequestBody UserDTO userDTO) {
	     System.out.println("로그인 요청 받음: " + userDTO);
	     String id = userDTO.getId();

	     // 아이디로 사용자 조회
	     SignupEntity signupEntity = loginService.findUserById(id);
	     System.err.println(signupEntity);

	     // 사용자가 존재하지 않으면 로그인 실패 처리
	     if (signupEntity == null) {
	         return ResponseEntity.status(401).body("로그인 실패");
	     }
	     // 기존 토큰 생성 로직
	     Map<String, String> tokens = generateTokens(signupEntity);
	     System.err.println(tokens);
	     String accessToken = tokens.get("access_token");

	     // 토큰 생성 및 설정
	     LoginDTO loginDTO = new LoginDTO();
	     loginDTO.setAccess_token("Bearer " + accessToken);

	     // 액세스 토큰을 쿠키에 담아서 클라이언트에게 전달
	     Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
	     accessTokenCookie.setHttpOnly(true);
	     accessTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 만료 시간 (예: 7일)
	     accessTokenCookie.setPath("/"); // 쿠키 경로 설정
	     response.addCookie(accessTokenCookie);
	     // HTTPONLY 쿠키 설정
	        HttpHeaders headers = setRefreshCookie(tokens.get("refresh_token"));

	     // 로그인 확인 로직
	     signupEntity = loginService.checklogin(userDTO.getId(), userDTO.getPassword());
	     System.err.println(signupEntity);
	     if (signupEntity != null) {
	         // 업데이트된 헤더와 함께 성공 상태의 ResponseEntity 반환	
	    	 sendTokenToOtherServer(accessToken);
	         return ResponseEntity.status(200).headers(headers).body(loginDTO.getAccess_token());
	     } else {
	         // 로그인 실패 처리
	         return ResponseEntity.status(401).body("로그인 실패");
	     }
	 }
	 
	 @PostMapping("/logout")
	 public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
	     // 쿠키 삭제
	     removeCookie(response, "accessToken");
	     removeCookie(response, "Refresh_token");

	     // 로그아웃 성공 응답
	     return new ResponseEntity<>("로그아웃 됩니다. <br>권한이 없습니다", HttpStatus.OK);
	 }

	 private void removeCookie(HttpServletResponse response, String cookieName) {
	     Cookie cookie = new Cookie(cookieName, "");
	     cookie.setMaxAge(0); 
	     cookie.setPath("/");
	     cookie.setHttpOnly(true); // httponly 설정 유지
	     response.addCookie(cookie);
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

		    // RestTemplate을 미리 생성하여 재사용
		    RestTemplate restTemplate = new RestTemplate();
		    System.err.println("여기도?");
		    
		    // HTTP POST 요청
		    ResponseEntity<String> responseEntity = restTemplate.postForEntity(otherServerUrl, requestEntity, String.class);
		}
	 
	 private Map<String, String> generateTokens(SignupEntity member) {
	        // jti 생성
	        String jti = JwtUtil.generateJti();
	        // 액세스 토큰 생성
	        String access_token = JwtUtil.generateAccessToken(member.getId(), member.getName(), member.getRole(), jti);
	        // 리프레시 토큰 생성
	        String refresh_token = JwtUtil.generateRefreshToken(member.getId(), member.getName(), member.getRole(),  jti);
	       
	        // RefreshToken을 DB에 저장한다. 성능 때문에 DB가 아니라 Redis에 저장하는 것이 좋다.
	        RefreshToken refreshTokenEntity = new RefreshToken();
	        refreshTokenEntity.setValue(refresh_token);
	        refreshTokenEntity.setId(member.getUserno());
	        refreshTokenService.addRefreshToken(refreshTokenEntity);

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

