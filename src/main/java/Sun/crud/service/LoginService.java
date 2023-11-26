package Sun.crud.service;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import Sun.crud.entity.SignupEntity;
import Sun.crud.repository.SignupRepository;
@Service
public class LoginService {
    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SignupEntity checklogin(String id, String password) {
        Optional<SignupEntity> signupEntityOptional = signupRepository.findById(id);

        if (signupEntityOptional.isPresent()) {
            SignupEntity signupEntity = signupEntityOptional.get();
            if (passwordEncoder.matches(password, signupEntity.getPassword())) {
                return signupEntity;
            }
        }

        return null;
    }
	
public ResponseCookie setRefeshCookie(String token) {
		
		String value = "Bearer " + token;
		String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
		ResponseCookie refresh_token = ResponseCookie
			.from("Refresh_token", encodedValue)
			.path("/authz")
			.sameSite("none")
			.secure(true)
			.httpOnly(true)
			.build();
		
		System.out.println("리프레시 토큰 생성 : " + refresh_token);
		
		return refresh_token;
	}


}
