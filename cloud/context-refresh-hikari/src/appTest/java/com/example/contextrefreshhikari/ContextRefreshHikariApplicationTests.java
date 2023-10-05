package com.example.contextrefreshhikari;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.example.contextrefreshhikari.entities.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import org.springframework.cr.smoketest.support.assertj.AssertableOutput;
import org.springframework.cr.smoketest.support.junit.ApplicationTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({ "JUnitMalformedDeclaration" })
@ApplicationTest
public class ContextRefreshHikariApplicationTests {

	// Bring the config file back to init values before next app start,
	// since the app updates the property file after properties set
	@AfterAll
	static void cleanUp() throws IOException {
		editExternalConfigurationProperties();
	}

	@Test
	void connectionMetadata(WebTestClient webClient) {
		webClient.get().uri("/metadata").exchange().expectStatus().isOk().expectBody().consumeWith(result -> {
			String connectionString = new String(result.getResponseBodyContent());
			assertThat(connectionString).startsWith("jdbc:postgresql");
			assertThat(connectionString).endsWith("database2");
		});
	}

	@Test
	void users(WebTestClient webTestClient, AssertableOutput output) {
		// We've connected to a different db after restore, so it should not have any
		// users
		webTestClient.get().uri("/users/add").exchange().expectStatus().isOk();

		webTestClient.get()
			.uri("/users")
			.header("ContentType", MediaType.APPLICATION_JSON_VALUE)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(User.class)
			.hasSize(2);
	}

	private static void editExternalConfigurationProperties() throws IOException {
		Files.writeString(Path.of("./dev.properties"), """
				spring.datasource.url: jdbc:postgresql://${DB1_HOST:localhost}:${DB1_PORT_5432:5432}/database1
				spring.datasource.username=user1
				spring.datasource.password=passwd1
				""", Charset.defaultCharset(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}
