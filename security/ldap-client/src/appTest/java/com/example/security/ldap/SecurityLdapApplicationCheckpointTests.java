package com.example.security.ldap;

import org.junit.jupiter.api.Test;

import org.springframework.cr.smoketest.support.junit.ApplicationTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
public class SecurityLdapApplicationCheckpointTests {

	@Test
	void homeShouldShowUsername(WebTestClient client) {
		client.get()
			.uri("/users")
			.headers((header) -> header.setBasicAuth("user", "password"))
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith((result) -> assertThat(new String(result.getResponseBodyContent())).contains("Rod Johnson"));
	}

}
