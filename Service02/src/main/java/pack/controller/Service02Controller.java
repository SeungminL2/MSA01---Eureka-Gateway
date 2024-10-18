package pack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/lion")    // GatewayServer > application.yml > predicates
public class Service02Controller {
	
	@GetMapping("")
	public String f2() {
		System.out.println("서비스02 컨트롤러");
		return "redirect:/";
	}
}
