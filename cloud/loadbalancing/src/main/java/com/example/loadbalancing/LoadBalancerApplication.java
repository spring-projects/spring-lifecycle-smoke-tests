package com.example.loadbalancing;

import com.example.loadbalancing.service.LoadBalancerClientTestService;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class LoadBalancerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadBalancerApplication.class, args);
	}

	@RestController
	static class TestController {

		@Autowired
		private LoadBalancerClientTestService testService;

		@GetMapping("/")
		public Flux<String> test() {
			return testService.callServices();
		}

	}

}
