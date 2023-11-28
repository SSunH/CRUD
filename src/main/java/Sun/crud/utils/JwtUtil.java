package Sun.crud.utils;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import Sun.crud.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtUtil {
	
public static String SECRET_KEY;

	@Autowired
    private CustomUserDetailsService userDetailsService;
	
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
    Boolean isTokenValid(String token) throws ExpiredJwtException, SignatureException {
        if (isTokenExpired(token)) {
            throw new ExpiredJwtException(null, null, "Token is expired");
        }

        // 여기에 추가적인 유효성 검사를 수행할 수 있습니다.

        return true;
    }


	 // 토큰에서 모든 클레임을 추출하는 메서드
	 private Claims extractAllClaims(String token) {
	     Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	     System.out.println("Extracting all claims from token: " + claims);
	     return claims;
	 }

	 // 토큰에서 아이디를 추출하는 메서드
	 private String extractUsername(String token) {
	     String username = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
	     System.out.println("Extracting username from token: " + username);
	     return username;
	 }

	 // 토큰의 만료 여부를 확인하는 메소드
	 private Boolean isTokenExpired(String token) {
	     Date expirationDate = extractExpiration(token);
	     boolean isExpired = expirationDate.before(new Date());
	     System.out.println("Is token expired? " + isExpired);
	     return isExpired;
	 }

	 // 토큰에서 만료 시간을 추출하는 메소드
	 private Date extractExpiration(String token) {
	     Date expiration = extractClaim(token, Claims::getExpiration);
	     System.out.println("Extracting expiration from token: " + expiration);
	     return expiration;
	 }

	 // 토큰에서 클레임을 추출하는 메서드
	 private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	     Claims claims = extractAllClaims(token);
	     T extractedClaim = claimsResolver.apply(claims);
	     System.out.println("Extracted claim from token: " + extractedClaim);
	     return extractedClaim;
	 }
 	
// // 토큰 유효성 검사 및 처리
//    private boolean validateAndHandleToken(String token, HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//        System.err.println(token);
//        System.err.println("토큰 유효성 ?");
//
//        if (isTokenValid(token)) {
//            // 토큰 검증 및 처리 성공
//            chain.doFilter(request, response);
//            return true;
//        } else {
//            System.err.println("여기야? 유효성 실패");
//            // 토큰이 유효하지 않은 경우 또는 다른 이유로 처리에 실패한 경우
//            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 유효하지 않습니다.");
//            return true;
//        }
//    }
//
//    // 주어진 token으로부터 사용자 ID를 추출
//    public static String getUserIdFromToken(String token) {
//        String subToken = token.substring(7);
//        // Claims는 JWT의 페이로드 (Payload)를 나타내는 본문 부분
//        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken).getBody();
//        System.out.println("사용자 아이디 : " + claims.getSubject());
//        return claims.getSubject();
//    }
//
//    // 토큰의 서명 알고리즘을 검증하는 메소드
//    private boolean isValidAlgorithm(String token, HttpServletResponse response) throws IOException {
//        System.err.println("isValidAlgorithm 호출");
//
//        if (!isAlgorithmValid(token)) {
//            // 잘못된 서명 알고리즘
//            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "잘못된 토큰 서명 알고리즘");
//            return false;
//        }
//        return true;
//    }
//
////    // 주어진 token의 유효성을 검사하는 메소드
////    private boolean isTokenValid(String token) {
////    	System.err.println(token);
////        String username = extractUsername(token);
////        System.err.println(username);
////        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
////        System.err.println(userDetails);
////        return validateToken(token, userDetails);
////    }
//
//    private Boolean isTokenValid(String token) {
//	     if (isTokenExpired(token)) {
//	         return false;
//	     }
//
//	     // 여기에 추가적인 유효성 검사를 수행할 수 있습니다.
//	     System.err.println("성공?");
//
//	     return true;
//	 }
//    
//    
////    // 주어진 token으로부터 사용자 ID를 추출
////    public static String getUserIdFromToken(String token) {
////    	String subToken = token.substring(7);
////    	// Claims는 JWT의 페이로드 (Payload)를 나타내는 본문 부분
////        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken).getBody();
////        System.out.println("사용자 아이디 : " + claims.getSubject());
////        return claims.getSubject();
////    }
////
////
////
////
////	// 주어진 token으로부터 alg를 추출하여 검증
////	 	private boolean isValidAlgorithm(String token, HttpServletResponse response) throws IOException {
////	 	    if (!JwtUtil.isAlgorithmValid(token)) {
////	 	    	// 잘못된 서명 알고리즘
////	 	        sendErrorResponse(response, 403, "Invalid token signature algorithm");
////	 	        return false;
////	 	    }
////	 	    return true;
////	 	}
//	// 주어진 token으로부터 alg를 추출하여 검증
//    public static boolean isAlgorithmValid(String token) {
//        System.err.println("isAlgorithmValid 호출");
//        Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
//        String algorithmUsed = jwsClaims.getHeader().getAlgorithm();
//        return SIGNATURE_ALGORITHM.getValue().equals(algorithmUsed);
//    }
// // Access_token과 Refresh_token 나눠서 처리
//    public void handleToken(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String accessToken)
//            throws ServletException, IOException {
//        System.err.println("필터 시작" + accessToken);
//        if (validateAndHandleToken(accessToken, request, response, chain)) {
//            return;
//        } else {
//            // 올바르지 않은 토큰
//            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Invalid token");
//        }
//    }

 

//	// 토큰을 검증하고 인증 프로세스를 처리
//	private boolean validateAndProcessToken(String token, HttpServletRequest request) {
//		String subToken = token.substring(7);
//		System.err.println(subToken);
//	    if (token != null && token.startsWith("Bearer ")) {
//	        String username = extractUsername(subToken);
//	        // 사용자 이름이 null이 아니고, 현재 Security Context에 인증 정보가 없는 경우
//	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//	        	// 사용자 정보 로드
//	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//
//	            // 토큰이 유효한 경우 Security Context에 인증 정보 설정
//	            if (validateToken(subToken, userDetails)) {
//	            	// 인증 정보를 Security Context에 설정
//	                setAuthenticationInSecurityContext(userDetails, request);
//	                return true;
//	            }
//	        }
//	    }
//	    return false;
//	}
	
//	// Security Context에 사용자 인증 정보를 설정
//    public void setAuthenticationInSecurityContext(UserDetails userDetails, HttpServletRequest request) {
//    	// UsernamePasswordAuthenticationToken은 Spring Security에서 제공하는 Authentication의 구현체로
//    	// 사용자의 인증 정보를 나타냄 이 객체는 주로 사용자의 ID, 비밀번호, 그리고 권한 정보를 포함
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
//                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        // 요청에 대한 세부 정보 설정 (예: IP 주소, 세션 ID 등)
//        usernamePasswordAuthenticationToken.setDetails(
//        		// new WebAuthenticationDetailsSource().buildDetails(request)는 요청에 대한 세부 정보를 생성하는 역할. 이 정보는 후속 보안 작업에서 사용
//        		new WebAuthenticationDetailsSource().buildDetails(request));
//        // SecurityContext에 Authentication 객체를 설정하는 역할. Authentication 객체는 Spring Security의 다른 부분에서 현재 사용자의 인증 정보를 접근하는데 사용
//        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//    }
	
//	// 토큰에서 아이디를 추출하는 메서드
//    private String extractUsername(String token) {
//        try {
//            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
//        } catch (Exception e) {
//            // 예외가 발생할 경우, 로깅 또는 예외 처리 로직을 추가할 수 있습니다.
//            e.printStackTrace();
//            return null; // 또는 원하는 값을 반환
//        }
//    }

    
//    // 토큰의 유효성을 검사하는 메소드
//    private Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

//    // 토큰의 만료 여부를 확인하는 메소드
//    public Boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // 토큰에서 만료 시간을 추출하는 메소드
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // 토큰에서 클레임을 추출하는 메서드
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // 토큰에서 모든 클레임을 추출하는 메서드
//    public Claims extractAllClaims(String token) {
//        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//    }
// 	
// 	// 쿠키에서 refreshToken 반환
//    public static String getRefreshTokenFromCookies(Cookie[] cookies) {
//        String refreshToken = null;
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("Refresh_token".equals(cookie.getName())) {
//                    String encodedRefreshToken = cookie.getValue();
//                    refreshToken = new String(Base64.getDecoder().decode(encodedRefreshToken));
//                    break;
//                }
//            }
//        }
//        return refreshToken; // 쿠키에서 "Refresh_token"을 찾지 못한 경우 null 반환
//    }
}
	
