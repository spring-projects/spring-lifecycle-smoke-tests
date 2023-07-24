/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.data.redis;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Strobl
 */
@Component
@EnableScheduling
class RedisReader implements SmartLifecycle {

	static final String STATE_KEY = "component-state";
	static final String COUNTER_KEY = "counter";

	final StringRedisTemplate redisTemplate;

	final AtomicBoolean running = new AtomicBoolean(false);

	RedisReader(StringRedisTemplate redisTemplate) {

		this.redisTemplate = redisTemplate;
		this.redisTemplate.delete(COUNTER_KEY);
		this.redisTemplate.opsForValue().set(STATE_KEY, "initialized");
	}

	@Override
	public void start() {

		if (running.compareAndSet(false, true)) {
			String previousState = getSetState("started");
			System.out.println("Starting RedisReader: was %s".formatted(previousState));
		}
	}

	@Scheduled(fixedDelay = 1000)
	public void scheduled() {

		if (isRunning()) {
			Long value = redisTemplate.opsForValue().increment(COUNTER_KEY);
			System.out.println("RedisReader: counting %s".formatted(value));
		}
	}

	@Override
	public void stop() {

		if (running.compareAndSet(true, false)) {
			String previousState = getSetState("stopped");
			System.out.println("Stopping RedisReader: was %s".formatted(previousState));
		}
	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

	String getSetState(String newState) {
		return redisTemplate.opsForValue().getAndSet(STATE_KEY, newState);
	}

}
