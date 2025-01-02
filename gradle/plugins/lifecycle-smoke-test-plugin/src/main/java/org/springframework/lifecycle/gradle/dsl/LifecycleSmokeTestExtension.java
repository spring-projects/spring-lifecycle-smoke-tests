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

package org.springframework.lifecycle.gradle.dsl;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

/**
 * DSL extension for configuring lifecycle smoke tests.
 *
 * @author Andy Wilkinson
 * @author Sebastien Deleuze
 */
public class LifecycleSmokeTestExtension {

	private final Property<Boolean> webApplication;

	private final Property<String> checkpointEvent;

	private final Expectation appTest;

	private final Expectation checkpointRestoreAppTest;

	private final Expectation test;


	@Inject
	public LifecycleSmokeTestExtension(Project project) {
		ObjectFactory objects = project.getObjects();
		this.webApplication = objects.property(Boolean.class);
		this.checkpointEvent = objects.property(String.class);
		this.appTest = objects.newInstance(Expectation.class, project);
		this.checkpointRestoreAppTest = objects.newInstance(Expectation.class, project);
		this.test = objects.newInstance(Expectation.class, project);
	}

	/**
	 * Whether the application under test is a web application.
	 * @return whether the application under test is a web application.
	 */
	public Property<Boolean> getWebApplication() {
		return this.webApplication;
	}

	/**
	 * Return the Spring event class name to be used to trigger a checkpoint.
	 * @return the event class name
	 */
	public Property<String> getCheckpointEvent() {
		return this.checkpointEvent;
	}

	/**
	 * Expectations for {@code appTest}.
	 */
	public Expectation getAppTest() {
		return this.appTest;
	}

	/**
	 * Configure expectations for {@code appTest}.
	 */
	public void appTest(Action<Expectation> action) {
		action.execute(this.appTest);
	}

	/**
	 * Expectations for {@code checkpointRestoreAppTest}.
	 */
	public Expectation getCheckpointRestoreAppTest() {
		return this.checkpointRestoreAppTest;
	}

	/**
	 * Configure expectations for {@code checkpointRestoreAppTest}.
	 */
	public void checkpointRestoreAppTest(Action<Expectation> action) {
		action.execute(this.checkpointRestoreAppTest);
	}

	/**
	 * Expectations for {@code test}.
	 */
	public Expectation getTest() {
		return this.test;
	}

	/**
	 * Configure expectations for {@code test}.
	 */
	public void test(Action<Expectation> action) {
		action.execute(this.test);
	}

	public static class Expectation {
		private final Property<Outcome> outcome;
		@Inject
		public Expectation(Project project) {
			this.outcome = project.getObjects().property(Outcome.class);
			this.outcome.convention(Outcome.SUCCESS);
		}
		/**
		 * The expected outcome.
		 */
		public Property<Outcome> getOutcome() {
			return this.outcome;
		}
		/**
		 * Note that expected outcome is failure
		 */
		public void expectedToFail(Action<Object> action) {
			this.outcome.set(Outcome.FAILURE);
		}
	}
	public static enum Outcome {
		/**
		 * The expected outcome is failure.
		 */
		FAILURE,
		/**
		 * The expected outcome is success.
		 */
		SUCCESS
	}


}
