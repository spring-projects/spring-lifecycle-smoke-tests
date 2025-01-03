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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;

/**
 * {@link Plugin} for lifecycle smoke test CI.
 *
 * @author Andy Wilkinson
 */
public class LifecycleSmokeTestCiPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		NamedDomainObjectContainer<SmokeTests> smokeTests = project.getObjects()
			.domainObjectContainer(SmokeTests.class);
		project.getExtensions().add("smokeTests", smokeTests);
		TaskProvider<Sync> syncWorkflows = project.getTasks().register("syncGitHubActionsWorkflows", Sync.class);
		syncWorkflows.configure((sync) -> {
			sync.into(".github/workflows");
			syncFromClasspath("smoke-test.yml", sync);
			syncFromClasspath("validate-gradle-wrapper.yml", sync);
		});
		smokeTests.configureEach((tests) -> {
			TaskProvider<Exec> describeSmokeTestsForBranch = project.getTasks()
				.register("describeSmokeTestsFor" + tests.getName(), Exec.class);
			describeSmokeTestsForBranch.configure((task) -> {
				task.setWorkingDir(new File(tests.getLocation()));
				task.commandLine("./gradlew", "describeSmokeTests", "--no-scan");
			});
			TaskProvider<GenerateGitHubActionsWorkflows> generateWorkflowsForBranch = project.getTasks()
				.register("generateGitHubActionsWorkflowsFor" + tests.getName(), GenerateGitHubActionsWorkflows.class);
			generateWorkflowsForBranch.configure((task) -> {
				task.dependsOn(describeSmokeTestsForBranch);
				task.getSpringBootGeneration().set(tests.getName());
				if (tests.getBranch() != null) {
					task.getGitBranch().set(tests.getBranch());
				}
				task.getSmokeTests().set(project.provider(() -> loadSmokeTests(tests.getLocation())));
				task.getCronSchedule().set(tests.getCronSchedule());
			});
			syncWorkflows.configure((sync) -> sync.from(generateWorkflowsForBranch));
		});
		TaskProvider<UpdateStatusPage> updateStatusPage = project.getTasks()
			.register("updateStatusPage", UpdateStatusPage.class);
		updateStatusPage.configure((task) -> {
			task.dependsOn(syncWorkflows);
			Map<String, List<SmokeTest>> allSmokeTests = new LinkedHashMap<>();
			smokeTests.forEach((tests) -> {
				List<SmokeTest> testsForGeneration = loadSmokeTests(tests.getLocation());
				allSmokeTests.put(tests.getName(), testsForGeneration);
			});
			task.getSmokeTests().set(allSmokeTests);
			task.getOutputFile().set(project.getLayout().getProjectDirectory().file("STATUS.adoc"));
		});
		project.getTasks().register("updateInfrastructure", (task) -> task.dependsOn(syncWorkflows, updateStatusPage));
	}

	private List<SmokeTest> loadSmokeTests(String location) {
		File[] smokeTests = new File(location + "/build/smoke-tests").listFiles();
		return (smokeTests == null ?
				Collections.emptyList() :
				Stream.of(smokeTests).map(this::load).map(SmokeTest::new).toList());
	}

	private Properties load(File file) {
		Properties properties = new Properties();
		try (FileInputStream input = new FileInputStream(file)) {
			properties.load(input);
			return properties;
		}
		catch (IOException ex) {
			throw new GradleException("Failed to load smoke test properties from '" + file + "'", ex);
		}
	}

	private void syncFromClasspath(String name, Sync sync) {
		sync.from(sync.getProject().getResources().getText().fromUri(getClass().getClassLoader().getResource(name)),
				(spec) -> spec.rename((temp) -> name));
	}

}
