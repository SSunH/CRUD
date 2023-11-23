package Sun.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Sun.crud.entity.SignupEntity;
import Sun.crud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {	
	@Autowired
	private UserService userService;
	
	 @RequestMapping(value = "/logingo", method = RequestMethod.GET)
	    public String login(){
	        return "login";
	    }
	 @RequestMapping(value = "/logingo", method = RequestMethod.POST)
	    public String loginPost() {
	        // 로그인 처리 로직 작성
	        // ...

	        // 로그인 성공 시 "/callback"으로 리다이렉트
	        return "redirect:/callback";
	    }
	
//	@ResponseBody
//	@PostMapping("/loginok")
//	public ResponseEntity<String> loginok(@RequestParam String id, @RequestParam String password){
//		System.err.println(id + password);
//        SignupEntity signupEntity = userService.checklogin(id, password);
//
//        if (signupEntity != null) {
//            // 로그인 성공 시 토큰 발행
//            String token = userService.login(signupEntity.getName(), signupEntity.getRole());
//            // 발행된 토큰을 클라이언트에게 응답
//            return ResponseEntity.ok().body(token);
//        } else {
//            // 로그인 실패
//            return ResponseEntity.ok().body("로그인 실패");
//        }
//	}
}

