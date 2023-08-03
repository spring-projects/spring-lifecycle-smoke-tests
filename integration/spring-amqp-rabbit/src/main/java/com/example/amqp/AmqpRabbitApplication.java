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
package com.example.amqp;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class AmqpRabbitApplication implements SmartLifecycle {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(AmqpRabbitApplication.class, args);
	}

	@Autowired
	RabbitTemplate template;

	@RabbitListener(id = "cf1", queues = "cf1")
	@SendTo
	public String upperCaseIt(String in) {
		try {
			String two = sendWithConfirms();
			return in.toUpperCase() + two;
		}
		catch (Exception e) {
			throw new AmqpRejectAndDontRequeueException("fail");
		}
	}

	@RabbitListener(id = "cf2", queues = "cf2")
	@SendTo
	public String lowerCaseIt(String in) {
		return in.toLowerCase();
	}

	private String sendWithConfirms() throws Exception {
		CorrelationData data = new CorrelationData();
		String result = (String) this.template.convertSendAndReceive("", "cf2", "TWO", data);
		data.getFuture().get(10, TimeUnit.SECONDS);
		return result;
	}

	@Bean
	public Queue queue1() {
		return new Queue("cf1");
	}

	@Bean
	public Queue queue2() {
		return new Queue("cf2");
	}

	@Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
	public void doSendMessage() {
		if (this.isRunning.get()) {
			System.out.println("++++++ Received: " + template.convertSendAndReceive("", "cf1", "one"));
		}
	}

	// Use the SmartLifecycle to synch the message sending with the checkpoint/restore.
	private AtomicBoolean isRunning = new AtomicBoolean(false);

	@Override
	public void start() {
		this.isRunning.set(true);
	}

	@Override
	public void stop() {
		this.isRunning.set(false);
	}

	@Override
	public boolean isRunning() {
		return this.isRunning.get();
	}

}
