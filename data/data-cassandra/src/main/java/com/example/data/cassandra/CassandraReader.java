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
package com.example.data.cassandra;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@EnableScheduling
class CassandraReader implements SmartLifecycle {

	private final Object lifecycleMonitor = new Object();

	private final AtomicInteger counter = new AtomicInteger(0);

	private State state = State.INITIALIZED;

	private List<String> userNames;

	@Autowired
	CqlSession cqlSession;

	enum State {

		INITIALIZED, STARTED, STOPPED

	}

	@Autowired
	UserRepository users;

	@Override
	public void start() {

		synchronized (lifecycleMonitor) {
			switch (state) {
				case INITIALIZED, STOPPED -> {

					System.out.printf("Starting CassandraReader: was %s\n", state);
					if (ObjectUtils.isEmpty(userNames)) {
						userNames = insertUsers().stream().map(User::getUsername).collect(Collectors.toList());
					}
					state = State.STARTED;
				}
			}
		}
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduled() {

		if (isRunning()) {

			int count = counter.incrementAndGet();
			User user = users.findUserByUsername(userNames.get(count % userNames.size()));
			System.out.printf("count %03d: %s\n", count, user);
		}
	}

	@Override
	public void stop() {

		synchronized (lifecycleMonitor) {
			switch (state) {
				case INITIALIZED, STARTED -> {
					System.out.printf("Stopping CassandraReader: was %s\n", state);
					state = State.STOPPED;
				}
			}
		}
	}

	@Override
	public boolean isRunning() {
		return State.STARTED.equals(state);
	}

	private List<User> insertUsers() {

		User brandonSanderson = new User(1L, "Brandon", "Sanderson");
		User brentWeeks = new User(2L, "Brent", "Weeks");
		User peterVBrett = new User(3L, "Peter V.", "Brett");

		return Streamable.of(users.saveAll(List.of(brandonSanderson, brentWeeks, peterVBrett))).toList();
	}

}
