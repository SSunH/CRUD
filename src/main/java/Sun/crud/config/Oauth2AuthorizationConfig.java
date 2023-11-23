package Sun.crud.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import Sun.crud.service.UserDetailService;

@EnableAuthorizationServer
@Configuration
public class Oauth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailService userDetailService;
	@Autowired
    private DataSource dataSource;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception{
			security.tokenKeyAccess("permitAll()");
			security.checkTokenAccess("isAuthenticated()");
			security.allowFormAuthenticationForClients();
					
	}
	
	
	// client 설정
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // 클라이언트 정보는 메모리를 이용 한다.
                .withClient("clientId") // 클라이언트 아이디
                .secret(passwordEncoder.encode("secretKey")) // 여기도 암호화 해줘야 합니다
                .authorizedGrantTypes("authorization_code","password", "refresh_token") // 가능한 토큰 발행 타입
                .scopes("read", "write") // 가능한 접근 범위
                .accessTokenValiditySeconds(60) // 토큰 유효 시간 : 1분
                .refreshTokenValiditySeconds(60*60) // 토큰 유효 시간 : 1시간
                .redirectUris("http://localhost:8080/callback") // 가능한 redirect uri
                .autoApprove(true); // 권한 동의는 자동으로 yes (false 로 할시 권한 동의 여부를 묻는다.)
    }    

    //Oauth 토큰 db에 저장
    @Bean
    public TokenStore tokenStore() {
    	return new JdbcTokenStore(dataSource);
    }
    // 인증, 토큰 설정
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	 // refrash token 발행시 유저 정보 검사 하는데 사용하는 서비스 설정 
        endpoints.authenticationManager(authenticationManager);
        // password 오류해결
        endpoints.userDetailsService(userDetailService);
        endpoints.tokenStore(tokenStore());
    }
 
    
   

}
