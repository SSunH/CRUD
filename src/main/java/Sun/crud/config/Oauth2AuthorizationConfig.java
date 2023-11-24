package Sun.crud.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

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
			//security.checkTokenAccess("isAuthenticated()"); JWT로 변경
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
//    @Bean
//    public TokenStore tokenStore() {
//    	return new JdbcTokenStore(dataSource);
//    }
    // 인증, 토큰 설정
    @Bean
    public TokenEnhancer tokenEnhancer() {
    	return new CustomTokenEnhancer();
    }
    
    @Bean TokenStore tokenStore() {
    	return new JwtTokenStore(jwtAccessTokenConverter());
    }
    

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	 TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
         tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter())); // 토큰 enhancer 객체 생성    
         
        endpoints.authenticationManager(authenticationManager) // grant_type password를 사용하기 위함 (manager 지정 안할시 password type 으로 토큰 발행시 Unsupported grant type: password 오류 발생)
                .userDetailsService(userDetailService) // refrash token 발행시 유저 정보 검사 하는데 사용하는 서비스 설정
                //.tokenStore(tokenStore()) // jwt 로 변경시 토큰 저장하지 않아도 리소스 서버에서 차제적으로 체크 가능
                .tokenEnhancer(tokenEnhancerChain) // enhancer 설정
                .tokenStore(tokenStore());
    }
    
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
    	  // RSA 암호화 : 비 대칭키 암호화 : 공개키로 암호화 하면 개인키로 복호화
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwtkey.jks"), "sunho1234".toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwtkey"));
        System.err.println("JwtAccessTokenConverter is configured.");
        
        return converter;
    }

    
   

}
