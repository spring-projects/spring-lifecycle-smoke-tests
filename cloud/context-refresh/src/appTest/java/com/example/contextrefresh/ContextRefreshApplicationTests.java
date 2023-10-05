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

@SuppressWarnings({ "JUnitMalformedDeclaration", "DataFlowIssue" })
@ApplicationTest
public class ContextRefreshApplicationTests {

	// Bring the config file back to init values before next app start,
	// since the app updates the property file after properties set
	@AfterAll
	static void cleanUp() throws IOException {
		editExternalConfigurationProperties();
	}

	@Test
	void refreshScopeBean(WebTestClient webClient) {
		webClient.get()
			.uri("/refresh")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(result -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("propValNew"));
	}

	@Test
	void configurationProperties(WebTestClient webClient) {
		webClient.get()
			.uri("/simple")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(result -> assertThat(new String(result.getResponseBodyContent())).isEqualTo("testValNew"));
	}

	private static void editExternalConfigurationProperties() throws IOException {
		Files.writeString(Path.of("./dev.properties"), """
				simple.test=testVal
				test=propVal
				""", Charset.defaultCharset(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}
