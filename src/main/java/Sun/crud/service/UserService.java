package Sun.crud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import Sun.crud.utils.JwtUtil;

@Service
public class UserService {
	
	@Value("${jwt.secretKey}")
	private String secretKey;
	
	private Long expiredMS = 1000 * 60 * 60l;

	public String login(String name, String password) {
		
		return JwtUtil.createJWT(name, secretKey, expiredMS);
	}
}
