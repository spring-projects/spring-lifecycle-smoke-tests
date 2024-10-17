/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.si;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@SpringBootApplication
@EnableIntegration
public class FileCopySpringIntegrationApplication {

	private final Log logger = LogFactory.getLog(getClass());

	public static final String INPUT_DIR = tempFolder("source_dir");

	public static final String OUTPUT_DIR = tempFolder("dest_dir");

	public String FILE_PATTERN = "*.txt";

	public static void main(String[] args) {
		SpringApplication.run(FileCopySpringIntegrationApplication.class, args);
	}

	@Bean
	public MessageChannel fileChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel transformedFileChannel() {
		return new DirectChannel();
	}

	@Transformer(inputChannel = "fileChannel", outputChannel = "transformedFileChannel")
	public String transform(String payload) {
		// System.out.println("Source File Payload:" + payload);
		logger.info("Source File Payload:" + payload);
		return payload;
	}

	@Bean
	@InboundChannelAdapter(channel = "fileChannel", poller = @Poller(fixedDelay = "1000"))
	public MessageSource<File> fileReadingMessageSource() {
		FileReadingMessageSource sourceReader = new FileReadingMessageSource();
		sourceReader.setDirectory(new File(INPUT_DIR));
		sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));
		return sourceReader;
	}

	@Bean
	@ServiceActivator(inputChannel = "transformedFileChannel")
	public MessageHandler fileWritingMessageHandler() {
		FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));
		handler.setFileExistsMode(FileExistsMode.REPLACE);
		handler.setExpectReply(false);
		return handler;
	}

	private static String tempFolder(String suffix) {
		String tmpDirsLocation = System.getProperty("java.io.tmpdir");
		return Paths.get(tmpDirsLocation, suffix).toString();
	}

}
