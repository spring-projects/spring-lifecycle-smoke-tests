package com.example.contextrefresh;

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
	public void doSomethingAfterStartup() throws IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Updating file values");
		}
		editExternalConfigurationProperties("""
				simple.test=testValNew
				test=propValNew
				""");
	}

	@GetMapping("/reset")
	void reset() throws IOException {
		editExternalConfigurationProperties("""
				simple.test=testVal
				test=propVal
				""");
	}

	private void editExternalConfigurationProperties(String newFileContent) throws IOException {
		Files.writeString(Path.of(path), newFileContent, Charset.defaultCharset(), StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

}
