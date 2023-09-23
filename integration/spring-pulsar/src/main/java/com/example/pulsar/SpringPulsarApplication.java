package com.example.pulsar;

import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.annotation.PulsarReader;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.pulsar.core.PulsarTopic;

@SpringBootApplication
public class SpringPulsarApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringPulsarApplication.class, args);
	}

	@Autowired
	private PulsarTemplate<Greeting> pulsarTemplate;

	@EventListener
	public void applicationReady(ApplicationReadyEvent ignored) throws PulsarClientException {
		Greeting message = new Greeting("Hello from CRaC!");
		pulsarTemplate.send("crac-demo-topic1", message, Schema.JSON(Greeting.class));
		System.out.println("Message Sent (@PulsarListener): " + message);

		pulsarTemplate.send("crac-demo-topic2", message, Schema.JSON(Greeting.class));
		System.out.println("Message Sent (@PulsarReader): " + message);
	}

	@PulsarListener(topics = "crac-demo-topic1", subscriptionName = "crac-demo-sub1")
	void receiveMessageFromTopic(Greeting message) {
		System.out.println("Message Received (@PulsarListener): " + message);
	}

	@PulsarReader(topics = "crac-demo-topic2", subscriptionName = "crac-demo-sub2", startMessageId = "earliest")
	void readMessageFromTopic(Greeting message) {
		System.out.println("Message Received (@PulsarReader): " + message);
	}

	@Bean
	PulsarTopic readerTopic() {
		return PulsarTopic.builder("crac-demo-topic2").numberOfPartitions(1).build();
	}

	record Greeting(String message) {
	}

}
