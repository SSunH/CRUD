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
public class SignupEntity{
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

}
