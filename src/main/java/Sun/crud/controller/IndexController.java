package Sun.crud.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class IndexController {
	
	@Controller
	public class YourController {

	    @GetMapping("/index")
	    public String index() {
	    	return "index";
	      
	    
	    }
	}

	
	@GetMapping("/cookie")
	public String cookie(){
		return "cookie";
	}

}
