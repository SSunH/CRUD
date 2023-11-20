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
import Sun.crud.service.SignupService;

@Controller
public class SignupController {
	
	@Autowired
	private SignupService signupService;
	
	@GetMapping("/signup")
	public String SignupShow() {
		return "signup";
	}
	
	@PostMapping("/signgo")
	public ResponseEntity<?> ok(@RequestBody UserDTO userDTO){
		System.err.println(userDTO);
		signupService.insertUser(userDTO);
		
        return new ResponseEntity<>("Signup successful", HttpStatus.OK);

	}
	
	@PostMapping("/checkID")
	@ResponseBody
    public String checkID(@RequestParam("id") String id) {
        int result = signupService.checkID(id);
        return result + "";
    }
}
