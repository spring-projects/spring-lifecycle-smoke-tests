package com.example.contextrefresh;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContextRefreshTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@AfterEach
	void cleanUp() {
		restTemplate.getForObject("/reset", Void.class);
	}

	// Verify for initial values only
	@Test
	void refreshScopeBean() {
		String property = this.restTemplate.getForObject("/refresh", String.class);
		assertThat(property).isEqualTo("propVal");
	}

	// Verify for initial values only
	@Test
	void configurationProperties() {
		String property = this.restTemplate.getForObject("/simple", String.class);
		assertThat(property).isEqualTo("testVal");
	}

}
