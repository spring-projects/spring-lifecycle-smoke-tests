package com.example.contextrefreshhttp;

import com.example.contextrefreshhttp.config.SimpleConfigurationProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SimpleConfigurationProperties.class)
public class ContextRefreshHttpApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContextRefreshHttpApplication.class, args);
	}

}