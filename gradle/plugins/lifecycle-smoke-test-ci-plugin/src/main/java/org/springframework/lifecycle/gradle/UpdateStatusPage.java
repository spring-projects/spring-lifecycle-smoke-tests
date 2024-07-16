/*
 * Copyright 2022-2024 the original author or authors.
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

package org.springframework.lifecycle.gradle;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

/**
 * Task to update the {@code STATUS.adoc} with the smoke tests.
 *
 * @author Stephane Nicoll
 */
public abstract class UpdateStatusPage extends DefaultTask {

	@OutputFile
	public abstract RegularFileProperty getOutputFile();

	@Input
	public abstract MapProperty<String, List<SmokeTest>> getSmokeTests();

	@TaskAction
	void updateStatusPage() throws IOException {
		List<String> lines = new ArrayList<>();
		lines.add("= Smoke Tests Status");
		lines.add(":toc:");
		lines.add("");
		lines.add("Check each test for potential configuration guidance.");
		lines.add("");
		getSmokeTests().get().forEach((name, tests) -> handleLocation(lines, name, tests));
		Files.write(getOutputFile().get().getAsFile().toPath(), lines);
	}

	private void handleLocation(List<String> content, String generation, List<SmokeTest> smokeTests) {
		content.add(":toc-title: %s Projects".formatted(generation));
		content.add("== %s Projects".formatted(generation));
		content.add("");
		Map<String, SortedSet<SmokeTest>> groupedSmokeTests = new TreeMap<>();
		for (SmokeTest smokeTest : smokeTests) {
			groupedSmokeTests
				.computeIfAbsent(smokeTest.group(), (group) -> new TreeSet<>(Comparator.comparing(SmokeTest::name)))
				.add(smokeTest);
		}
		groupedSmokeTests.forEach((group, tests) -> {
			content.add("=== " + capitalize(group));
			content.add("");
			content.add("[%header,cols=\"2\"]");
			content.add("|===");
			content.add("h|Smoke Test");
			content.add("h|Status");
			content.add("");
			for (SmokeTest test : tests) {
				String name = test.name();
				String workflowUrl = workflowUrl(generation, group, name);
				content.add("|" + testUrl(group, name) + "[" + name + "]");
				content.add("| image:%s/badge.svg[\"Status\", link=\"%s\"]".formatted(workflowUrl, workflowUrl));
				content.add("");
			}
			content.add("|===");
			content.add("");
		});
	}

	private String workflowUrl(String generation, String group, String name) {
		return "https://github.com/spring-projects/spring-lifecycle-smoke-tests/actions/workflows/%s-%s-%s.yml"
			.formatted(generation, group, name);
	}

	private String capitalize(String input) {
		StringBuffer buffer = new StringBuffer(input.length());
		for (char c : input.toCharArray()) {
			buffer.append(buffer.isEmpty() ? Character.toUpperCase(c) : c);
		}
		return buffer.toString();
	}

	private String testUrl(String group, String name) {
		return "https://github.com/spring-projects/spring-lifecycle-smoke-tests/tree/main/" + group + "/" + name;
	}

	private enum TestType {

		APP_TEST(SmokeTest::appTests, "-app-test", "appTest"),
		CR_APP_TEST(SmokeTest::appTests, "-cr-app-test", "checkpointRestoreAppTest"),
		TEST(SmokeTest::tests, "-test", "test");

		private final Predicate<SmokeTest> predicate;

		private final String urlSuffix;

		private final String taskName;

		TestType(Predicate<SmokeTest> predicate, String suffix, String taskName) {
			this.predicate = predicate;
			this.urlSuffix = suffix;
			this.taskName = taskName;
		}

		String badge(SmokeTest smokeTest) {
			if (!this.predicate.test(smokeTest)) {
				return "";
			}
			return "image:" + badgeUrl(smokeTest.name(), this.urlSuffix) + "[link="
					+ jobUrl(smokeTest.name(), this.urlSuffix) + "]";
		}

		String taskName() {
			return this.taskName;
		}

		private String badgeUrl(String name, String suffix) {
			return workflowUrl(name) + "/badge.svg?branch=main";
		}

		private String jobUrl(String name, String suffix) {
			return workflowUrl(name);
		}

		private String workflowUrl(String name) {
			return "https://github.com/spring-projects/spring-lifecycle-smoke-tests/workflows/3.3.x-%s.yml"
				.formatted(name);
		}

	}

}
