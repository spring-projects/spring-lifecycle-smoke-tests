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

package org.springframework.cr.smoketest.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Output from an application.
 *
 * @author Andy Wilkinson
 * @author Sebastien Deleuze
 */
public class Output {

	private final Path outputPath;

	private final Path errorPath;

	private Output(Path outputPath, Path errorPath) {
		this.outputPath = outputPath;
		this.errorPath = errorPath;
	}

	public List<String> outputLines() {
		try {
			return Files.readAllLines(this.outputPath);
		}
		catch (IOException ex) {
			throw new RuntimeException();
		}
	}

	public List<String> errorLines() {
		try {
			return Files.readAllLines(this.errorPath);
		}
		catch (IOException ex) {
			throw new RuntimeException();
		}
	}

	public static Output current() {
		String outputProperty = System.getProperty("org.springframework.cr.smoketest.standard-output");
		if (outputProperty == null) {
			throw new IllegalStateException(
					"Standard output is not available as org.springframework.cr.smoketest.standard-output "
							+ "system property has not been set");
		}
		String errorProperty = System.getProperty("org.springframework.cr.smoketest.standard-error");
		if (errorProperty == null) {
			throw new IllegalStateException(
					"Standard error is not available as org.springframework.cr.smoketest.standard-error "
							+ "system property has not been set");
		}
		return new Output(new File(outputProperty).toPath(), new File(errorProperty).toPath());
	}

}
