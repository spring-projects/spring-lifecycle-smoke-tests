package com.example.contextrefresh;

import com.example.contextrefresh.config.SimpleConfigurationProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SimpleConfigurationProperties.class)
public class ContextRefreshApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContextRefreshApplication.class, args);
	}

}