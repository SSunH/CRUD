package Sun.crud.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Sun.crud.entity.SignupEntity;
import Sun.crud.repository.SignupRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private SignupRepository signupRepository;    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SignupEntity signupEntity = signupRepository.findById(username);
        if (signupEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
       
     // Status 정보를 GrantedAuthority로 변환
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(signupEntity.getRole()));
        return new org.springframework.security.core.userdetails.User(signupEntity.getId(), signupEntity.getPassword(), authorities);
    }
   }

