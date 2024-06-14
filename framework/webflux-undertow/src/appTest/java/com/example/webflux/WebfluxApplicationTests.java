package com.example.webflux;

import org.junit.jupiter.api.Test;

import org.springframework.lifecycle.smoketest.support.junit.ApplicationTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
class WebfluxApplicationTests {

	@Test
	void stringResponseBody(WebTestClient client) {
		client.get()
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith((result) -> assertThat(new String(result.getResponseBodyContent()))
				.isEqualTo("Hello from Spring WebFlux and Undertow"));
	}

	@Test
	void resourceInStatic(WebTestClient client) {
		client.get()
			.uri("foo.html")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith((result) -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("Foo"));
	}

}
