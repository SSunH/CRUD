package Sun.crud.utils;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
	
	public static String createJWT(String name, String role, String secretKey, Long expiredMS) {		
		// 토큰에 저장할부분
		Claims claims = Jwts.claims();
		claims.put("name", name);
		claims.put("role", role);
		
		return Jwts.builder()
				.setClaims(claims) //토큰에 저장할 내용
				.setIssuedAt(new Date(System.currentTimeMillis())) //시간
				.setExpiration(new Date(System.currentTimeMillis() + expiredMS))
				.signWith(SignatureAlgorithm.HS256, secretKey) // 알고리즘으로 사인됨
				.compact();
	}

}
