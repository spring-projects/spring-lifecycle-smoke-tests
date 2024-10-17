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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import org.springframework.lifecycle.smoketest.support.assertj.AssertableOutput;
import org.springframework.lifecycle.smoketest.support.junit.ApplicationTest;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
public class FileCopySpringIntegrationApplicationTest {

	@Test
	void connectionTest(AssertableOutput output) {

		String fileName = "test-" + UUID.randomUUID() + ".txt";
		String sourceFilePath = tempFolder("source_dir") + File.separator + fileName;
		String destFilePath = tempFolder("dest_dir") + File.separator + fileName;

		writeFile("Hello World", sourceFilePath);

		Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
			assertThat(new File(destFilePath)).exists();
			assertThat(output).hasLineContaining("Source File Payload:" + sourceFilePath);
		});
	}

	private static String tempFolder(String suffix) {
		String tmpDirsLocation = System.getProperty("java.io.tmpdir");
		return Paths.get(tmpDirsLocation, suffix).toString();
	}

	private static void writeFile(String fileBody, String filePath) {
		try {
			StreamUtils.copy(fileBody, StandardCharsets.UTF_8, new FileOutputStream(filePath));
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to copy test file to source directory", e);
		}
	}

}