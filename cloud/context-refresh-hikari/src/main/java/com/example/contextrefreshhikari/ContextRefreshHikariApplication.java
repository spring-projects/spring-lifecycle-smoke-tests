package com.example.contextrefreshhikari;

import com.example.contextrefreshhikari.entities.User;
import com.example.contextrefreshhikari.repositories.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ContextRefreshHikariApplication {

	private static final Log LOG = LogFactory.getLog(ContextRefreshHikariApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ContextRefreshHikariApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(UserRepository userRepository) {
		return (String[] args) -> {
			User user1 = new User("John", "john@domain.com");
			User user2 = new User("Julie", "julie@domain.com");
			userRepository.save(user1);
			userRepository.save(user2);
			userRepository.findAll().forEach(LOG::info);
		};
	}

}
