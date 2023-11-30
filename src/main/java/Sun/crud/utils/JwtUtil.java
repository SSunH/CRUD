package Sun.crud.utils;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import Sun.crud.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
public static String SECRET_KEY;

	@Autowired
	private CustomUserDetailsService UserDetailsService;

	
    @Value("${jwt.secretKey}")
    public void setSecretKey(String SECRET_KEY) {
    	JwtUtil.SECRET_KEY = SECRET_KEY;
    }
    // 시그니쳐 알고리즘 설정
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    
	// 토큰 만료시간 설정
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;		// 15분 15 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;	// 24시간, 24시간/일 * 60분/시간 * 60초/분 * 1000밀리초/초
    
    // jti 생성
    public static String generateJti() {
        return UUID.randomUUID().toString();
    }
    
    // ACCESS_TOKEN 생성
    public static String generateAccessToken(String id, String name, String role, String jti) {
    	// 새로운 JWT를 생성하기 위한 Builder 객체를 초기화
        return Jwts.builder()
        		// JWT의 sub (subject) 필드를 설정합니다. 이 필드는 토큰이 대상이 되는 주체(일반적으로 사용자)를 식별
                .setSubject(id)
                // 추가적인 claim으로 name을 설정. claim은 추가적인 데이터를 저장할 수 있는 key-value 쌍
                .claim("name", name)
                .claim("role", role)
                // WT의 jti (JWT ID) 필드를 설정합니다. 이 필드는 토큰의 고유 식별자로 사용됩니다. 이를 통해 토큰이 한 번만 사용되도록 할 수 있으며, 랜덤한 UUID를 생성하여 이를 ID로 사용
                .setId(jti)  // 랜덤한 UUID를 jti (JWT ID)로 사용
                // JWT의 exp (expiration time) 필드를 설정	
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                // WT를 서명하는 데 사용될 알고리즘과 시크릿 키를 설정
                .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
                // 최종적으로 생성된 JWT를 직렬화하여 문자열 형태로 반환
                .compact();
    }
    
    // REFRESH_TOKEN 생성
    public static String generateRefreshToken(String userId, String name, String role, String jti) {
        return Jwts.builder()
            .setSubject(userId)         
            .setId(jti)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
            .compact();
    }
    
 	
    // 오류 응답 전송
    public void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.sendError(status, message);
    }
    
    
 // 토큰의 유효성을 검사하는 메소드
    Boolean isTokenValid(String token) {
        if (isTokenExpired(token)) {
            System.err.println("Token is expired");
            return false; // 토큰이 만료되었을 경우 유효하지 않음
        }

        // 여기에 추가적인 유효성 검사를 수행할 수 있습니다.

        System.out.println("토큰이 유효합니다.");
        return true; // 토큰이 만료되지 않았고 추가적인 유효성 검사에 통과했을 경우 유효함
    }
	 // 토큰에서 모든 클레임을 추출하는 메서드
	 private Claims extractAllClaims(String token) {
		 System.err.println("토큰 유효성 검사 진행합니다.");
	     Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	     System.out.println("토큰에서 추출한 클레임: " + claims);
	     return claims;
	 }

	 // 토큰의 만료 여부를 확인하는 메소드
	 private Boolean isTokenExpired(String token) {
	     Date expirationDate = extractExpiration(token);
	     boolean isExpired = expirationDate.before(new Date());
	     System.out.println("토큰이 만료되었습니까?: " + isExpired);
	     return isExpired;
	 }

	 // 토큰에서 만료 시간을 추출하는 메소드
	 private Date extractExpiration(String token) {
	     Date expiration = extractClaim(token, Claims::getExpiration);
	     System.out.println("토큰 만료 일자 : " + expiration);
	     return expiration;
	 }

	 // 토큰에서 클레임을 추출하는 메서드
	 private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	     Claims claims = extractAllClaims(token);
	     T extractedClaim = claimsResolver.apply(claims);
	     System.out.println("토큰에서 추출된 클레임: " + extractedClaim);
	     return extractedClaim;
	 }
	

    // 주어진 token으로부터 사용자 ID를 추출 리프레쉬 토큰 재발급 시
    public static String getUserIdFromToken(String tokenWithoutBearer) {
        // Claims는 JWT의 페이로드 (Payload)를 나타내는 본문 부분
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(tokenWithoutBearer).getBody();
        System.out.println("사용자 아이디 : " + claims.getSubject());
        return claims.getSubject();
       }
    
 // 토큰을 검증하고 인증 프로세스를 처리
 	public boolean validateAndProcessToken(String token, HttpServletRequest request) {
 		System.err.println("2번째 SecurityContext 사용자 정보 저장 시작"); 	
 		
 	    if (token != null) { 	    	
 	        String username = extractUsername(token);
 	        System.err.println(username);
 	        // 사용자 이름이 null이 아니고, 현재 Security Context에 인증 정보가 없는 경우
 	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
 	        	// 사용자 정보 로드
 	            UserDetails userDetails = UserDetailsService.loadUserByUsername(username);
 	            System.err.println("사용자 정보 찾기");
 	            System.err.println(userDetails);
 	             setAuthenticationInSecurityContext(userDetails, request);
 	                return true;
 	            }
 	        }
 	    return false;
 	    }
 	
 	
	// Security Context에 사용자 인증 정보를 설정
    public void setAuthenticationInSecurityContext(UserDetails userDetails, HttpServletRequest request) {
        System.err.println("SecurityContext에 사용자의 인증 정보 설정: " + userDetails.getUsername());

    	// UsernamePasswordAuthenticationToken은 Spring Security에서 제공하는 Authentication의 구현체로
    	// 사용자의 인증 정보를 나타냄 이 객체는 주로 사용자의 ID, 비밀번호, 그리고 권한 정보를 포함
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        System.err.println(usernamePasswordAuthenticationToken);
        // 요청에 대한 세부 정보 설정 (예: IP 주소, 세션 ID 등)
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        // SecurityContext에 Authentication 객체를 설정하는 역할. Authentication 객체는 Spring Security의 다른 부분에서 현재 사용자의 인증 정보를 접근하는데 사용
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        System.err.println("SecurityContext 안에 유저 정보 넣기 성공");        

    }
 	
 	// 토큰에서 아이디를 추출하는 메서드
     private String extractUsername(String token) {
    	 Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    	 System.err.println(claims);
         return claims.get("sub", String.class);
     }          
	
	
}

	
