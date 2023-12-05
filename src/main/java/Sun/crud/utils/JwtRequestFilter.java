package Sun.crud.utils;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import Sun.crud.entity.RefreshToken;
import Sun.crud.entity.SignupEntity;
import Sun.crud.repository.SignupRepository;
import Sun.crud.service.refreshTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
	private refreshTokenService refreshTokenService;
	@Autowired
	private SignupRepository signupRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	        throws ServletException, IOException {

	    // 특정 엔드포인트에 대해서는 필터를 건너뛰도록 설정
	    if (isAllowedPath(request)) {
	        chain.doFilter(request, response);
	        return;
	    }

	    String refreshToken = null;
	    String tokenWithoutBearer = null;
	    
	    boolean accessTokenFound = false;

	    // 쿠키에서 엑세스토큰 가져옴
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("Refresh_token".equals(cookie.getName())) {
	                // 리프레쉬 토큰 처리
	                refreshToken = cookie.getValue();
	                System.err.println("리프레쉬 토큰:" + refreshToken);

	                // Base64 디코딩
	                byte[] decodedBytes = Base64.getDecoder().decode(refreshToken);
	                String decodedString = new String(decodedBytes);
	                System.out.println("디코딩된 리프레쉬 토큰:" + decodedString);
	                tokenWithoutBearer = extractTokenWithoutBearer(decodedString);
	                System.err.println("리프래쉬 토큰 : " + tokenWithoutBearer);
	            }
	        }

	        for (Cookie cookie : cookies) {
	            if ("accessToken".equals(cookie.getName())) {
	                // 엑세스 토큰 처리
	                String token = cookie.getValue();
	                System.out.println("엑세스 토큰 : " + token);

	                try {
	                    if (token != null && jwtUtil.isTokenValid(token)) {
	                        // 유효한 토큰이면 다음 단계로 진행
	                        if (jwtUtil.validateAndProcessToken(token, request)) {
	                            chain.doFilter(request, response);
	                        }
	                        accessTokenFound = true;
	                    }
	                } catch (ExpiredJwtException e) {
	                    // Access token이 만료된 경우 리프레시 토큰으로 갱신 시도
	                    System.err.println("AccessToken이 만료되었습니다.");
	                    System.err.println("새로운 AccessToken발급을 진행합니다.");
	                    refreshAccessToken(request, response, tokenWithoutBearer);

	                    // 리프레시 성공 후, 다시 시도
	                    return; // 여기서 메소드 종료
	                }

	            }
	        }	    
	    }
	 // 엑세스토큰만 없는 경우
	    if (!accessTokenFound) {
	        System.err.println("엑세스토큰이없다!");
            refreshAccessToken(request, response, tokenWithoutBearer);
            return;
	    }	    
	}
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response, String tokenWithoutBearer) {
        try {
            // 요청에서 리프레시 토큰 가져오기           

            // 리프레쉬 토큰 db에서 유효성 검사
        	System.err.println(tokenWithoutBearer);
            RefreshToken refreshToken = refreshTokenService.findRefreshToken(tokenWithoutBearer)
                    .orElseThrow(() -> new IllegalArgumentException("리프레쉬 토큰을 찾을 수 없습니다."));
            System.err.println("Refresh Token 정보: " + refreshToken);

            // 토큰에서 사용자 ID 추출
            String userId = JwtUtil.getUserIdFromToken(tokenWithoutBearer);
            System.err.println("토큰에서 추출한 아이디: " +userId);
            // DB에서 사용자 정보 조회
            SignupEntity signupEntity = signupRepository.findById(userId);

            // 사용자 유효성 검사
            if (signupEntity == null) {
                System.err.println("사용자가 없습니다.");
                return;
            }

            Map<String, String> newTokens = newgenerateTokens(signupEntity);
            // 새로운 엑세스 토큰
            String accessToken = newTokens.get("access_token");

            // 액세스 토큰을 쿠키에 담아서 클라이언트에게 전달
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 만료 시간 (예: 7일)
            accessTokenCookie.setPath("/"); // 쿠키 경로 설정
            response.addCookie(accessTokenCookie);
            sendTokenToOtherServer(accessToken);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", accessToken);
            System.err.println(responseBody);
            System.err.println("새로운 AccessToken 발급 완료!");
        } catch (Exception e) {
            System.err.println("리프레쉬 토큰 에러입니다: " + e.getMessage());
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
		    // RestTemplate을 미리 생성하여 재사용
		    RestTemplate restTemplate = new RestTemplate();		    
		    // HTTP POST 요청
		    ResponseEntity<String> responseEntity = restTemplate.postForEntity(otherServerUrl, requestEntity, String.class);
		    // 응답 코드 등을 확인할 수 있는 로깅 추가
		    HttpStatus statusCode = responseEntity.getStatusCode();
		    if (statusCode.is2xxSuccessful()) {
		    	System.err.println("success");
		    } else {
		    	System.err.println("fail");
		    }
		}	

	// 쿠키에서 refreshToken 반환
	    public static String getRefreshTokenFromCookies(Cookie[] cookies) {
	    	System.err.println(cookies);
	        String refreshToken = null;
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                if ("Refresh_token".equals(cookie.getName())) {
	                    String encodedRefreshToken = cookie.getValue();
	                    refreshToken = new String(Base64.getDecoder().decode(encodedRefreshToken));
	                    break;
	                }
	            }
	        }
	        return refreshToken; // 쿠키에서 "Refresh_token"을 찾지 못한 경우 null 반환
	    }	    	   
	    
	    private Map<String, String> newgenerateTokens(SignupEntity signupEntity) {
	        // jti 생성
	        String jti = JwtUtil.generateJti();
	        // 액세스 토큰 생성
	        String access_token = JwtUtil.generateAccessToken(signupEntity.getId(), signupEntity.getName(),signupEntity.getRole(), jti);
	        // 토큰들을 맵에 저장
	        Map<String, String> tokens = new HashMap<>();
	        tokens.put("access_token", access_token);
	        return tokens;
	    }
 

	private boolean isAllowedPath(HttpServletRequest request) {
        // "/login" 엔드포인트에 대해서는 필터를 건너뛰도록 설정
        String requestURI = request.getRequestURI();
        System.err.println("현재 접속 요청 " +requestURI);
        return "/login".equals(requestURI) || "/signup".equals(requestURI) || "/signgo".equals(requestURI) || "/checkID".equals(requestURI); 
    }
    private String extractTokenWithoutBearer(String tokenWithBearer) {
        // "Bearer "를 제거한 토큰 반환
        if (tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")) {
            return tokenWithBearer.substring(7);
        }
        return tokenWithBearer;
    }
}
