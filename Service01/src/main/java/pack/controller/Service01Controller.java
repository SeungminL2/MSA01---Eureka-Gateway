package pack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/tiger")    // GatewayServer > application.yml > predicates
public class Service01Controller {
	
	@GetMapping("")
	public String f1() {
		System.out.println("서비스01 컨트롤러");
		return "redirect:/";
	}
}
