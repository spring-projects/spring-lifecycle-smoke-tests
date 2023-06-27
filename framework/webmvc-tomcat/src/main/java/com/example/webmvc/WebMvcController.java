package com.example.webmvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebMvcController {

	@GetMapping("/")
	public String hello() {
		return "Hello from Spring MVC and Tomcat";
	}

}
