package com.example.webclient;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
class DataRequester implements SmartLifecycle {

	private final WebClient webClient;

	private volatile boolean running = false;

	DataRequester(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public void start() {
		if (!isRunning()) {
			this.running = true;
			http();
		}
	}

	@Override
	public void stop() {
		if (isRunning()) {
			this.running = false;
		}
	}

	private void http() {
		try {
			DataDto dto = this.webClient.get()
				.uri("/anything")
				.retrieve()
				.bodyToMono(DataDto.class)
				.timeout(Duration.ofSeconds(10))
				.block();
			System.out.printf("http: %s%n", dto);
		}
		catch (Exception ex) {
			System.out.println("http failed:");
			ex.printStackTrace(System.out);
		}
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

}
