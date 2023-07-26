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
package com.example.hibernate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Transactional
@EnableScheduling
class DataReader implements SmartLifecycle {

	private final Object lifecycleMonitor = new Object();

	private final AtomicInteger counter = new AtomicInteger(0);

	private State state = State.INITIALIZED;

	private List<Integer> authorIds;

	@PersistenceContext
	private EntityManager entityManager;

	enum State {

		INITIALIZED, STARTED, STOPPED

	}

	@Override
	public void start() {

		synchronized (lifecycleMonitor) {
			switch (state) {
				case INITIALIZED, STOPPED -> {
					System.out.printf("Starting DataReader: was %s\n", state);
					if (ObjectUtils.isEmpty(authorIds)) {
						authorIds = insertAuthors().stream().map(Author::getId).collect(Collectors.toList());
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
			Author author = entityManager.find(Author.class, authorIds.get(count % 3));
			System.out.printf("count %03d: %s\n", count, author);
		}
	}

	@Override
	public void stop() {

		synchronized (lifecycleMonitor) {
			switch (state) {
				case INITIALIZED, STARTED -> {
					System.out.printf("Stopping DataReader: was %s\n", state);
					state = State.STOPPED;
				}
			}
		}
	}

	@Override
	public boolean isRunning() {
		return State.STARTED.equals(state);
	}

	private List<Author> insertAuthors() {

		Author brandonSanderson = new Author(null, "Brandon Sanderson");
		Author brentWeeks = new Author(null, "Brent Weeks");
		Author peterVBrett = new Author(null, "Peter V. Brett");

		entityManager.persist(brandonSanderson);
		entityManager.persist(brentWeeks);
		entityManager.persist(peterVBrett);

		entityManager.flush();
		entityManager.getEntityManagerFactory().getCache().evictAll();

		Session session = entityManager.unwrap(Session.class);

		if (session != null) {
			session.clear(); // internal cache clear
		}

		return List.of(brandonSanderson, brentWeeks, peterVBrett);
	}

}
