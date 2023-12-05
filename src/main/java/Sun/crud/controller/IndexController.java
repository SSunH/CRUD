package Sun.crud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {
	
	@Controller
	public class YourController {

	    @GetMapping("/index")
	    public String index() {
	    	return "index";
	      
	    
	    }
	}	

}
