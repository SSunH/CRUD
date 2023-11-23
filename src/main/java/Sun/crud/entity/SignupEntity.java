package Sun.crud.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Data;

@Data
@Entity
@Table(name="user")
public class SignupEntity implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userNo", length = 5)
	@Comment("유저 구분 번호")
	private int userno;
	
	@Column(name = "password", nullable = false, length = 100)						
	@Comment("유저 비밀번호")
	private String password;
	
	@Column(name = "id", nullable = false, length = 15)
	@Comment("유저 아이디")
	private String id;
	
	@Column(name = "name", nullable = false, length = 15)
	@Comment("유저 이름")
	private String name;
	
	@Column(name = "email", nullable = false, length = 50)
	@Comment("유저 이메일")
	private String email;
	
	@Column(name = "role", nullable = false, length = 15)
	@Comment("유저 역할")
	private String role;
	
	@Column(name= "state", nullable = true, unique = false)
	@Comment("계정 상태")
    private String state; // Y : 정상 회원 , L : 잠긴 계정, P : 패스워드 만료, A : 계정 만료

    // security 기본 회원 정보인 UserDetails 클래스 implement 하기 위한 기본 함수들..

    // 권한 (기본 권한 셋팅)

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		return authorities;
	}

	@Override
	    public String getPassword() {
	        return this.password;
	    }

	    @Override
	    public String getUsername() {
	        return this.id;
	    }

	    // 계정 만료
	    @Override
	    public boolean isAccountNonExpired() {
	        if(StringUtils.equalsIgnoreCase(state, "A")){
	            return false;
	        }
	        return true;
	    }

	    // 잠긴 계정
	    @Override
	    public boolean isAccountNonLocked() {
	        if(StringUtils.equalsIgnoreCase(state, "L")){
	            return false;
	        }
	        return true;
	    }

	    // 패스워드 만료
	    @Override
	    public boolean isCredentialsNonExpired() {
	        if(StringUtils.equalsIgnoreCase(state, "P")){
	            return false;
	        }
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        if(isCredentialsNonExpired() && isAccountNonExpired() && isAccountNonLocked()){
	            return true;
	        }
	        return false;
	    }
	
	

}
