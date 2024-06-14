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

package org.springframework.lifecycle.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.crac.CheckpointException;
import org.crac.Core;
import org.crac.RestoreException;

import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * {@link ApplicationListener} trigger a checkpoint when the application is fully started
 * when the {@code org.springframework.lifecycle.smoketest.checkpoint} JVM system property
 * is set to {@code onApplicationReady}.
 */
class CheckpointListener implements ApplicationListener<ApplicationEvent> {

	private static final String CHECKPOINT_EVENT_PROPERTY_NAME = "org.springframework.lifecycle.smoketest.checkpoint";

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		String eventClassName = System.getProperty(CHECKPOINT_EVENT_PROPERTY_NAME);
		if (event.getClass().getName().equalsIgnoreCase(eventClassName)) {
			new CracDelegate().checkpointRestore(eventClassName);
		}
	}

	private class CracDelegate {

		public void checkpointRestore(String eventClassName) {
			logger.info("Triggering JVM checkpoint/restore on " + eventClassName);
			try {
				Core.checkpointRestore();
			}
			catch (UnsupportedOperationException ex) {
				throw new ApplicationContextException("CRaC checkpoint not supported on current JVM", ex);
			}
			catch (CheckpointException ex) {
				throw new ApplicationContextException("Failed to take CRaC checkpoint on refresh", ex);
			}
			catch (RestoreException ex) {
				throw new ApplicationContextException("Failed to restore CRaC checkpoint on refresh", ex);
			}
		}

	}

}
