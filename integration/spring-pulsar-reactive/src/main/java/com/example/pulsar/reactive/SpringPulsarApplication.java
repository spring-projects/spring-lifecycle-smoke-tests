package com.example.pulsar.reactive;

import org.apache.pulsar.client.api.Schema;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.pulsar.core.PulsarTopic;
import org.springframework.pulsar.reactive.config.annotation.ReactivePulsarListener;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;

@SpringBootApplication
public class SpringPulsarApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringPulsarApplication.class, args);
	}

	@Autowired
	private ReactivePulsarTemplate<Greeting> reactivePulsarTemplate;

	@EventListener
	public void applicationReady(ApplicationReadyEvent ignored) {
		String topic = "crac-demo-reactive-topic1";
		Greeting message = new Greeting("Hello from CRaC!");
		reactivePulsarTemplate.send(topic, message, Schema.JSON(Greeting.class)).subscribe();
		System.out.println("Message Sent (ReactivePulsarTemplate): " + message);
	}

	@ReactivePulsarListener(topics = "crac-demo-reactive-topic1", subscriptionName = "crac-demo-reactive-sub1")
	Mono<Void> receiveMessageFromTopic(Greeting message) {
		System.out.println("Message Received (@ReactivePulsarListener): " + message);
		return Mono.empty();
	}

	@Bean
	PulsarTopic listenerTopic() {
		return PulsarTopic.builder("crac-demo-reactive-topic1").numberOfPartitions(1).build();
	}

	record Greeting(String message) {
	}

}
