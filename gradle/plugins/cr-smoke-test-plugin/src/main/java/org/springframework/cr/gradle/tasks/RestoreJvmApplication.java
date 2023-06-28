package org.springframework.cr.gradle.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Task;
import org.gradle.internal.jvm.Jvm;

/**
 * {@link Task} to restore an application on the JVM.
 *
 * @author Sebastien Deleuze
 */
public abstract class RestoreJvmApplication extends StartApplication {

	@Override
	protected ProcessBuilder prepareProcessBuilder(ProcessBuilder processBuilder) {
		File executable = Jvm.current().getJavaExecutable();
		List<String> command = new ArrayList<>();
		command.add(executable.getAbsolutePath());
		command.add("-XX:CRaCRestoreFrom=" + getOutputDirectory().get());
		return processBuilder.command(command);
	}
}
