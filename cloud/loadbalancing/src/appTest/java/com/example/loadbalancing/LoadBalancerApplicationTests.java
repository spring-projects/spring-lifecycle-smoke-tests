package com.example.loadbalancing;

import org.junit.jupiter.api.Test;

import org.springframework.cr.smoketest.support.junit.ApplicationTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
class LoadBalancerApplicationTests {

	@Test
	void loadBalancing(WebTestClient webClient) {
		webClient.get()
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(result -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("testdemo"));
	}

}
