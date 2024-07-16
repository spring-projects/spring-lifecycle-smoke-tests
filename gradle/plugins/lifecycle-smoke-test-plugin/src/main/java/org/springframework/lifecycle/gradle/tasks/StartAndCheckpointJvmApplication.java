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

package org.springframework.lifecycle.gradle.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Task;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.jvm.Jvm;

/**
 * {@link Task} to start and create a checkpoint of an application on the JVM.
 *
 * @author Sebastien Deleuze
 */
public abstract class StartAndCheckpointJvmApplication extends StartApplication {

	public StartAndCheckpointJvmApplication() {
		getOutputFile().convention(getOutputDirectory().map((dir) -> dir.file("output-checkpoint.txt")));
		getErrorFile().convention(getOutputDirectory().map((dir) -> dir.file("error-checkpoint.txt")));
		getPidFile().convention(getOutputDirectory().map((dir) -> dir.file("pid")));
	}

	@Override
	protected ProcessBuilder prepareProcessBuilder(ProcessBuilder processBuilder) {
		String outputDirectory = getOutputDirectory().get().toString();
		File executable = Jvm.current().getJavaExecutable();
		List<String> command = new ArrayList<>();
		command.add("/bin/bash");
		command.add("-c");
		StringBuilder builder = new StringBuilder(executable.getAbsolutePath());
		builder.append(" -Dorg.springframework.lifecycle.smoketest.checkpoint=");
		builder.append(getCheckpointEvent().get());
		builder.append(" -XX:CRaCCheckpointTo=");
		builder.append(outputDirectory);
		if (getWebApplication().get()) {
			builder.append(" -Dserver.port=0");
		}
		builder.append(" -jar ");
		builder.append(getApplicationBinary().get().getAsFile().getAbsolutePath());
		builder.append(" > ");
		builder.append(getOutputFile().get().getAsFile().getAbsolutePath());
		builder.append(" 2> ");
		builder.append(getErrorFile().get().getAsFile().getAbsolutePath());
		command.add(builder.toString());
		return processBuilder.command(command);
	}

	/**
	 * Override to remove the redirect to output files that triggers an error during
	 * restoration.
	 */
	@Override
	@TaskAction
	void startApplication() throws IOException {
		getOutputDirectory().getAsFile().get().mkdirs();
		File redirectedError = getErrorFile().get().getAsFile();
		File redirectedOutput = getOutputFile().get().getAsFile();
		Path pid = getPidFile().get().getAsFile().toPath();
		Files.deleteIfExists(redirectedError.toPath());
		Files.deleteIfExists(redirectedOutput.toPath());
		Files.deleteIfExists(pid);
		ProcessBuilder processBuilder = prepareProcessBuilder(new ProcessBuilder());
		processBuilder.environment().putAll(getInternalEnvironment().get());
		Process process = processBuilder.start();
		Files.write(pid, List.of(Long.toString(process.pid())));
	}

	/**
	 * Return the Spring event class name to be used to trigger a checkpoint.
	 * @return the even class name
	 */
	@Input
	public abstract Property<String> getCheckpointEvent();

}
