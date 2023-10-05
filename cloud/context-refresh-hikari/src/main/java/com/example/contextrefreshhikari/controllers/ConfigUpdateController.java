package com.example.contextrefreshhikari.controllers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigUpdateController {

	private static final Log LOG = LogFactory.getLog(ConfigUpdateController.class);

	@Value("${config.path}")
	private String path;

	@EventListener(ApplicationStartedEvent.class)
	public void editExternalConfig() throws IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Updating file values");
		}
		editExternalConfigurationProperties("""
				spring.datasource.url: jdbc:postgresql://${DB2_HOST:localhost}:${DB2_PORT_5432:5432}/database2
				spring.datasource.username=user2
				spring.datasource.password=passwd2
				""");
	}

	@GetMapping("/reset")
	void reset() throws IOException {
		editExternalConfigurationProperties("""
				spring.datasource.url: jdbc:postgresql://${DB1_HOST:localhost}:${DB1_PORT_5432:5432}/database1
				spring.datasource.username=user1
				spring.datasource.password=passwd1
				""");
	}

	private void editExternalConfigurationProperties(String newFileContent) throws IOException {
		Files.writeString(Path.of(path), newFileContent, Charset.defaultCharset(), StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

}
