package Sun.crud.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Sun.crud.DTO.UserDTO;
import Sun.crud.entity.SignupEntity;
import Sun.crud.service.SignupService;

@Controller
public class SignupController {
	
	@Autowired
	private SignupService signupService;
	
	@GetMapping("/signup")
	public String SignupShow() {
		return "signup";
	}
	@ResponseBody
	@PostMapping("/signgo")
	public ResponseEntity<?> ok(@RequestBody UserDTO userDTO){
		System.err.println(userDTO);
		SignupEntity signupEntity = new SignupEntity();
		signupEntity.setId(userDTO.getId());
		signupEntity.setEmail(userDTO.getEmail());
		signupEntity.setName(userDTO.getName());
		signupEntity.setPassword(userDTO.getPassword());
		signupEntity.setRole(userDTO.getRole());
		
		signupService.insertUser(signupEntity);
		
        return ResponseEntity.ok("ok");
	}
	
	@ResponseBody
	@PostMapping("/checkID")
    public String checkID(@RequestParam("id") String id) {
        int result = signupService.checkID(id);
        return result + "";
    }
}
