package com.example.contextrefresh;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import org.springframework.cr.smoketest.support.junit.ApplicationTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
public class ContextRefreshApplicationTests {

	@AfterAll
	static void setUp() throws IOException {
		editExternalConfigurationProperties("""
				simple.test=testVal
				test=propVal
				""");
	}

	@Test
	void refreshScopeBean(WebTestClient webClient) throws IOException {
		webClient.get()
			.uri("/refresh")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(result -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("propVal"));
		editExternalConfigurationProperties("""
				simple.test=testVal
				test=propValNew
				""");
		webClient.post().uri("/actuator/refresh").exchange().expectStatus().isOk();

		webClient.get()
			.uri("/refresh")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(result -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("propValNew"));
	}

	@Test
	void configurationProperties(WebTestClient webClient) throws IOException {
		webClient.get()
			.uri("/simple")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(result -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("testVal"));
		editExternalConfigurationProperties("""
				simple.test=testValNew
				test=propVal
				""");
		webClient.post().uri("/actuator/refresh").exchange().expectStatus().isOk();

		webClient.get()
			.uri("/simple")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(result -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("testValNew"));
	}

	private static void editExternalConfigurationProperties(String newFileContent) throws IOException {
		Files.writeString(Path.of("./dev.properties"), newFileContent, Charset.defaultCharset(),
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}
