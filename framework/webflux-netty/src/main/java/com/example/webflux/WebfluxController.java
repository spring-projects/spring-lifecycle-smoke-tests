package com.example.webflux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebfluxController {

	@GetMapping("/")
	public String hello() {
		return "Hello from Spring WebFlux and Netty";
	}

}
