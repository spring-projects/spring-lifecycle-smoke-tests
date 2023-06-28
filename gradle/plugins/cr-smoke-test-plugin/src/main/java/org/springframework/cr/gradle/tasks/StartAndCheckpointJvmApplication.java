package org.springframework.cr.gradle.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.jvm.Jvm;

/**
 * {@link Task} to start and create a checkpoint of an application on the JVM.
 *
 * @author Sebastien Deleuze
 */
public abstract class StartAndCheckpointJvmApplication extends StartApplication {

	@Override
	protected ProcessBuilder prepareProcessBuilder(ProcessBuilder processBuilder) {
		File executable = Jvm.current().getJavaExecutable();
		List<String> command = new ArrayList<>();
		command.add(executable.getAbsolutePath());
		command.add("-Dorg.springframework.cr.smoketest.checkpoint=onApplicationReady");
		command.add("-XX:CRaCCheckpointTo=" + getOutputDirectory().get());
		if (getWebApplication().get()) {
			command.add("-Dserver.port=0");
		}
		command.add("-jar");
		command.add(getApplicationBinary().get().getAsFile().getAbsolutePath());
		return processBuilder.command(command);
	}

	/**
	 * Override to remove the redirect to output files that triggers an error during restoration
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


}
