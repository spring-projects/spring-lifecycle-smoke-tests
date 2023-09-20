package com.example.contextrefreshhttp;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContextRefreshHttpTests {

	@Autowired
	private TestRestTemplate restTemplate;

	private static final Resource resource = new ClassPathResource("dev.properties");

	@AfterAll
	static void setUp() throws IOException {
		editExternalConfigurationProperties("""
				simple.test=testVal
				test=propVal
				""");
	}

	@Test
	void refreshScopeBean() throws IOException {
		String property = this.restTemplate.getForObject("/refresh", String.class);
		assertThat(property).isEqualTo("propVal");
		editExternalConfigurationProperties("""
				simple.test=testVal
				test=propValNew
				""");

		RequestEntity<Void> request = RequestEntity.post("/actuator/refresh")
			.contentType(MediaType.APPLICATION_JSON)
			.build();

		this.restTemplate.postForObject("/actuator/refresh", request, String.class);

		String updatedProperty = this.restTemplate.getForObject("/refresh", String.class);
		assertThat(updatedProperty).isEqualTo("propValNew");
	}

	@Test
	void configurationProperties() throws IOException {
		String property = this.restTemplate.getForObject("/simple", String.class);
		assertThat(property).isEqualTo("testVal");
		editExternalConfigurationProperties("""
				simple.test=testValNew
				test=propVal
				""");

		RequestEntity<Void> request = RequestEntity.post("/actuator/refresh")
			.contentType(MediaType.APPLICATION_JSON)
			.build();

		this.restTemplate.postForObject("/actuator/refresh", request, String.class);

		String updatedProperty = this.restTemplate.getForObject("/simple", String.class);
		assertThat(updatedProperty).isEqualTo("testValNew");
	}

	static void editExternalConfigurationProperties(String newFileContent) throws IOException {
		Files.writeString(Paths.get(resource.getFile().getAbsolutePath()), newFileContent, Charset.defaultCharset(),
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}
