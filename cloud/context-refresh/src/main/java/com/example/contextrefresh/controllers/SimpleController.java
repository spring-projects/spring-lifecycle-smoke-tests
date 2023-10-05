package com.example.contextrefresh.controllers;

import com.example.contextrefresh.config.SimpleConfigurationProperties;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simple")
public class SimpleController {

	private final SimpleConfigurationProperties properties;

	public SimpleController(SimpleConfigurationProperties properties) {
		this.properties = properties;
	}

	@GetMapping
	String test() {
		return properties.getTest();
	}

}
