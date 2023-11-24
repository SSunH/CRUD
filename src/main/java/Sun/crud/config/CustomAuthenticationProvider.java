package Sun.crud.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import Sun.crud.service.UserDetailService;

@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailService userDetailService;

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    // 로그인 버튼 클릭시 사용자 체크 메소드
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        UserDetails info = userDetailService.loadUserByUsername(username);
        
        if (ObjectUtils.isEmpty(info)) {
            throw new UsernameNotFoundException("user not found");
        }

        // 수정된 부분: PasswordEncoder를 사용하여 비밀번호 비교
        if (!passwordEncoder.matches(password, info.getPassword())) {
            throw new UsernameNotFoundException("please password check");
        }


        return new UsernamePasswordAuthenticationToken(info, password, info.getAuthorities());
    }
}
