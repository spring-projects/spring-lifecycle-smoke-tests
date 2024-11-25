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
package com.example.data.r2dbc;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@EnableScheduling
class R2dbcReader implements SmartLifecycle {

	private final Object lifecycleMonitor = new Object();

	private final AtomicInteger counter = new AtomicInteger(0);

	List<String> customerNames;

	private State state = State.INITIALIZED;

	@Autowired
	CustomerRepository customers;

	@Autowired
	DatabaseClient database;

	@Autowired
	ConnectionFactory connectionFactory;

	enum State {

		INITIALIZED, STARTED, STOPPED

	}

	@Override
	public void start() {

		synchronized (lifecycleMonitor) {

			System.out.printf("Starting R2dbcReader: was %s\n", state);

			switch (state) {
				case INITIALIZED -> {

					var statements = Arrays.asList(//
							"DROP TABLE IF EXISTS customer;",
							"CREATE TABLE customer ( id SERIAL PRIMARY KEY, firstname VARCHAR(100) NOT NULL, lastname VARCHAR(100) NOT NULL);");

					statements.forEach(it -> database.sql(it) //
						.fetch() //
						.rowsUpdated() //
						.block());

					if (ObjectUtils.isEmpty(customerNames)) {

						var dave = new Customer(null, "Dave", "Matthews");
						var carter = new Customer(null, "Carter", "Beauford");
						this.customerNames = insertCustomers(dave, carter);
					}
					state = State.STARTED;
				}
				case STOPPED -> {

				}
			}
		}
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduled() {

		if (isRunning()) {

			customers.findByLastname(customerNames.get(counter.get() % customerNames.size())).doOnNext(customer -> {
				int count = counter.incrementAndGet();
				System.out.printf("count %03d: %s\n", count, customer);
			}).blockFirst(Duration.ofMillis(500));
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

	private List<String> insertCustomers(Customer... customers) {
		return this.customers.saveAll(Arrays.asList(customers)).map(Customer::lastname).collectList().block();
	}

}
