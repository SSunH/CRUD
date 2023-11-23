package Sun.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import Sun.crud.entity.SignupEntity;
import Sun.crud.repository.SignupRepository;
import Sun.crud.utils.JwtUtil;

@Service
public class UserService {
	@Autowired
	SignupRepository signupRepository;
	
	@Value("${jwt.secretKey}")
	private String secretKey;
	
	private Long expiredMS = 1000 * 60 * 60l;

	public String login(String name, String role) {
		// 로그인 성공 시 토큰 발행
		return JwtUtil.createJWT(name, role, secretKey, expiredMS);
	}

	// 실제 데이터베이스 연동을 통한 로그인 확인 로직이어야 합니다.
	public SignupEntity checklogin(String id, String password) {
        SignupEntity signupEntity = signupRepository.findByIdAndPassword(id, password);

		return signupEntity;
	}
}
