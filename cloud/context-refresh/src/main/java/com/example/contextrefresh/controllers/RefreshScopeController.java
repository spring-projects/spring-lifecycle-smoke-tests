package com.example.contextrefresh.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh")
@RefreshScope
public class RefreshScopeController {

	@Value("${test}")
	private String test;

	@GetMapping
	String test() {
		return test;
	}

}
