package com.example.resttemplate;

import java.net.URI;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
class DataRequester implements SmartLifecycle {

	private final RestTemplate restTemplate;

	private volatile boolean running = false;

	private final String httpHost;

	private final int httpPort;

	DataRequester(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		// TODO Check why run-dev-container.sh is broken by the commented line below
		// this.httpHost = env("HTTPBIN_HOST", "localhost");
		this.httpHost = "localhost";
		this.httpPort = env("HTTPBIN_PORT_8080", 8080);
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

	public void http() {
		try {
			DataDto dto = restTemplate.getForObject(
					URI.create("http://%s:%d/anything".formatted(this.httpHost, this.httpPort)), DataDto.class);
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

	private static String env(String name, String def) {
		String value = System.getenv(name);
		return StringUtils.hasLength(value) ? value : def;
	}

	private static int env(String name, int def) {
		String value = System.getenv(name);
		return StringUtils.hasLength(value) ? Integer.parseInt(value) : def;
	}

}
