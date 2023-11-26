package Sun.crud.utils;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
private static String SECRET_KEY;
	
    @Value("${jwt.secretKey}")
    public void setSecretKey(String SECRET_KEY) {
    	JwtUtil.SECRET_KEY = SECRET_KEY;
    }
    // 시그니쳐 알고리즘 설정
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    
	// 토큰 만료시간 설정
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;		// 15분
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
            .claim("name", name)
            .claim("role", role)
            .setId(jti)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
            .compact();
    }
}
	
