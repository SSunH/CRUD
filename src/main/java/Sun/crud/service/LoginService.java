package Sun.crud.service;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

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
        Optional<SignupEntity> signupEntityOptional = signupRepository.findOptionalById(id);

        if (signupEntityOptional.isPresent()) {
            SignupEntity signupEntity = signupEntityOptional.get();
            if (passwordEncoder.matches(password, signupEntity.getPassword())) {
                return signupEntity;
            }
        }

        return null;
    }
	
public ResponseCookie setRefeshCookie(String token) {
		System.err.println("너는 리프레쉬? " + token);
		String value = "Bearer " + token;
		String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
		ResponseCookie refresh_token = ResponseCookie
			.from("Refresh_token", encodedValue)
			.path("/auth")
			.sameSite("none")
			.secure(true)
			.httpOnly(true)
			.build();
		
		System.out.println("리프레시 토큰 생성 : " + refresh_token);
		
		return refresh_token;
	}    



public SignupEntity findUserById(String id) {
	return signupRepository.findUserById(id);
}







}
