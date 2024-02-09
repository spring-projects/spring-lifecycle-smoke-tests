package com.example.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
class WebClientConfiguration {

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		// TODO Check why run-dev-container.sh is broken by the commented line below
		// String host = env("HTTPBIN_HOST", "localhost");
		String host = "localhost";
		int port = env("HTTPBIN_PORT_8080", 8080);
		return builder.baseUrl("http://%s:%d/".formatted(host, port)).build();
	}

	private static String env(String name, String def) {
		String value = System.getenv(name);
		return StringUtils.hasLength(value) ? value : def;
	}

	private static int env(String name, int def) {
		String value = System.getenv(name);
		return StringUtils.hasLength(value) ? Integer.parseInt(value) : def;
	}

}
