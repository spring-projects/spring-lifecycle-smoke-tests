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
package com.example.data.elasticsearch;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.SmartLifecycle;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component
@EnableScheduling
public class ElasticsearchReader implements SmartLifecycle {

	private final ApplicationStateRepository applicationStateRepository;

	private final ElasticsearchTemplate elasticsearchOperations;

	final AtomicBoolean running = new AtomicBoolean(false);

	public ElasticsearchReader(ApplicationStateRepository applicationStateRepository,
			ElasticsearchTemplate elasticsearchOperations) {

		this.applicationStateRepository = applicationStateRepository;
		this.applicationStateRepository
			.save(new ApplicationState(ClassUtils.getShortName(ElasticsearchReader.class), "initialized"));
		this.elasticsearchOperations = elasticsearchOperations;
	}

	@Override
	public void start() {
		if (running.compareAndSet(false, true)) {
			ApplicationState previousState = getSetState("started");
			System.out.println("Starting ElasticsearchReader: was %s".formatted(previousState.getState()));
		}
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduled() {

		if (isRunning()) {
			String tagline = elasticsearchOperations.execute(client -> client.info().tagline());
			System.out.println(tagline);
		}
	}

	@Override
	public void stop() {
		if (running.compareAndSet(true, false)) {
			ApplicationState previousState = getSetState("stopped");
			System.out.println("Stopping ElasticsearchReader: was %s".formatted(previousState.getState()));
		}
	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

	ApplicationState getSetState(String newState) {

		ApplicationState current = applicationStateRepository
			.findById(ClassUtils.getShortName(ElasticsearchReader.class))
			.orElseThrow(() -> new IllegalStateException("Expected ElasticsearchReader to be initialized"));
		applicationStateRepository.save(current.newState(newState));
		return current;
	}

}
