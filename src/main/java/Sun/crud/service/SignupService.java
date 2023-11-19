package Sun.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Sun.crud.DTO.UserDTO;
import Sun.crud.entity.SignupEntity;
import Sun.crud.repository.SignupRepository;

@Service
public class SignupService {
	@Autowired
	private SignupRepository signupRepository;
	

	public void insertUser(UserDTO userDTO) {
		SignupEntity signupEntity = new SignupEntity();
		signupEntity.setId(userDTO.getId());
		signupEntity.setEmail(userDTO.getEmail());
		signupEntity.setName(userDTO.getName());
		signupEntity.setPassword(userDTO.getPassword());
		signupEntity.setRole(userDTO.getRole());
		signupRepository.save(signupEntity);
	}	
	 

}
