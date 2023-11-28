//package Sun.crud.controller;
//
//import java.util.Date;
//import java.util.function.Function;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//
//import Sun.crud.service.CustomUserDetailsService;
//import Sun.crud.utils.JwtUtil;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.lang.Arrays;
//
//
//
//@Controller
//public class TokenVerificationController {
//	
//	 @Value("${jwt.secretKey}")
//	 private String SECRET_KEY;
//	   
//	
//	
//	 @PostMapping("/verify")
//	 public ResponseEntity<String> verifyToken(HttpServletRequest request) {
//	     // 쿠키에서 accessToken이라는 이름의 토큰을 검색합니다.
//	     Cookie[] cookies = request.getCookies();
//	     System.err.println(cookies);
//	     System.err.println("여기야?");
//
//	     if (cookies != null) {
//	         for (Cookie cookie : cookies) {
//	             if ("accessToken".equals(cookie.getName())) {
//	                 String token = cookie.getValue();
//	                 System.err.println(token);
//	                 if (isTokenValid(token)) {
//	                     return ResponseEntity.ok("토큰이 유효합니다.");
//	                 } else {
//	                     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
//	                 }
//	             }
//	         }
//	     }
//
//	     // 만약 쿠키가 없거나 accessToken이라는 이름의 쿠키가 없는 경우
//	     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
//	 }
//
//	 // 토큰의 유효성을 검사하는 메소드
//	 private Boolean isTokenValid(String token) {
//	     if (isTokenExpired(token)) {
//	         return false;
//	     }
//
//	     // 여기에 추가적인 유효성 검사를 수행할 수 있습니다.
//
//	     return true;
//	 }
//
//	 // 토큰에서 모든 클레임을 추출하는 메서드
//	 private Claims extractAllClaims(String token) {
//	     Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//	     System.out.println("Extracting all claims from token: " + claims);
//	     return claims;
//	 }
//
//	 // 토큰에서 아이디를 추출하는 메서드
//	 private String extractUsername(String token) {
//	     String username = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
//	     System.out.println("Extracting username from token: " + username);
//	     return username;
//	 }
//
//	 // 토큰의 만료 여부를 확인하는 메소드
//	 private Boolean isTokenExpired(String token) {
//	     Date expirationDate = extractExpiration(token);
//	     boolean isExpired = expirationDate.before(new Date());
//	     System.out.println("Is token expired? " + isExpired);
//	     return isExpired;
//	 }
//
//	 // 토큰에서 만료 시간을 추출하는 메소드
//	 private Date extractExpiration(String token) {
//	     Date expiration = extractClaim(token, Claims::getExpiration);
//	     System.out.println("Extracting expiration from token: " + expiration);
//	     return expiration;
//	 }
//
//	 // 토큰에서 클레임을 추출하는 메서드
//	 private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//	     Claims claims = extractAllClaims(token);
//	     T extractedClaim = claimsResolver.apply(claims);
//	     System.out.println("Extracted claim from token: " + extractedClaim);
//	     return extractedClaim;
//	 }
//}
//
//////
//
//   
// //
////
//// 
////}
//
//
