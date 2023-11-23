package Sun.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import Sun.crud.entity.SignupEntity;
import Sun.crud.repository.UserRepository;

public class UserDetailService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.err.println(username);
		SignupEntity userInfo = userRepository.findById(username);
		
		if (userInfo == null) {
            throw new UsernameNotFoundException("사용자가 없습니다" + username);
        }

        return User.builder()
                .username(userInfo.getId())
                .password(userInfo.getPassword())                
                .roles(userInfo.getRole())
                .build();	
        }	

}
