package com.example.contextrefreshhikari.controllers;

import java.util.List;

import com.example.contextrefreshhikari.entities.User;
import com.example.contextrefreshhikari.repositories.UserRepository;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	List<User> getUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/add")
	void addUsers() {
		User user1 = new User("Steve", "steve@domain.com");
		User user2 = new User("Sarah", "sarah@domain.com");
		userRepository.save(user1);
		userRepository.save(user2);
	}

}
