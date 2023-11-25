package Sun.crud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter  {	
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // csrf 공격을 막기 위해 state 값을 전달 받지 않는다
                .formLogin() // 로그인 페이지 설정
                .loginPage("/logingo") // 커스텀 로그인 페이지 설정
                .and()
                .httpBasic(); // http 통신으로 basic auth를 사용 할 수 있다. (ex: Authorization: Basic bzFbdGfmZrptWY30YQ==)

    }
    

	   @Override
	    @Bean
	    public AuthenticationManager authenticationManagerBean() throws Exception {
	        return super.authenticationManagerBean();
	    }
	   //비밀번호 암호화
	   @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	 
	   
	   

}
