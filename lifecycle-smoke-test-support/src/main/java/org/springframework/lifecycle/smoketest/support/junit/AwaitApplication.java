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

package org.springframework.lifecycle.smoketest.support.junit;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.springframework.lifecycle.smoketest.support.Output;

/**
 * {@link BeforeAllCallback} that waits for the application to have started.
 *
 * @author Andy Wilkinson
 */
class AwaitApplication implements BeforeAllCallback {

	private static final Duration START_TIMEOUT = Duration.ofSeconds(20);

	private final Pattern APPLICATION_STARTED = Pattern
		.compile("Started [A-Za-z0-9]+ in [0-9\\.]+ seconds \\(process running for [0-9\\.]+\\)");

	private final Pattern APPLICATION_RE_STARTED = Pattern.compile("Spring-managed lifecycle restart completed");

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		Output output = Output.current();
		long end = Instant.now().plus(START_TIMEOUT).toEpochMilli();
		List<String> outputLines = null;
		while (System.currentTimeMillis() < end) {
			outputLines = output.outputLines();
			for (String line : outputLines) {
				if (this.APPLICATION_STARTED.matcher(line).find() || this.APPLICATION_RE_STARTED.matcher(line).find()) {
					return;
				}
			}
		}
		StringBuilder message = new StringBuilder(
				"Started log message was not detected within " + START_TIMEOUT.getSeconds() + "s:.");
		List<String> checkpointOutputLines = output.checkpointOutputLines();
		if (!checkpointOutputLines.isEmpty()) {
			message.append("\nStandard output for the checkpoint:\n");
			for (String line : checkpointOutputLines) {
				message.append(line + "\n");
			}
			message.append("\n");
		}
		message.append("\n");
		message.append("\n\nStandard output:\n");
		if (outputLines == null || outputLines.isEmpty()) {
			message.append("<< none >>");
		}
		else {
			for (String line : outputLines) {
				message.append(line + "\n");
			}
		}
		System.err.println(message.toString());
		throw new IllegalStateException(message.toString());
	}

}
