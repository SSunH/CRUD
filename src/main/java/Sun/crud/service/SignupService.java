package Sun.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Sun.crud.entity.SignupEntity;
import Sun.crud.repository.SignupRepository;

@Service
public class SignupService {
	@Autowired
	private SignupRepository signupRepository;
	
	public void insertUser(SignupEntity signupEntity) {
		signupRepository.save(signupEntity);		
	}		 
	public int checkID(String id) {
		return signupRepository.countById(id);
	}


	

}
