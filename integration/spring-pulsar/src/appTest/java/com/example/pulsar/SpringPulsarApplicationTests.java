package com.example.pulsar;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import org.springframework.cr.smoketest.support.assertj.AssertableOutput;
import org.springframework.cr.smoketest.support.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
public class SpringPulsarApplicationTests {

	@Test
	void pulsarTemplateSendsMessageToListenerTopic(AssertableOutput output) {
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.untilAsserted(() -> assertThat(output)
				.hasSingleLineContaining("Message Sent (@PulsarListener): Greeting[message=Hello from CRaC!]"));
	}

	@Test
	void pulsarTemplateSendsMessageToReaderTopic(AssertableOutput output) {
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.untilAsserted(() -> assertThat(output)
				.hasSingleLineContaining("Message Sent (@PulsarReader): Greeting[message=Hello from CRaC!]"));
	}

	@Test
	void pulsarListenerMethodReceivesMessage(AssertableOutput output) {
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.untilAsserted(() -> assertThat(output)
				.hasSingleLineContaining("Message Received (@PulsarListener): Greeting[message=Hello from CRaC!]"));
	}

	@Test
	void pulsarReaderMethodReceivesMessage(AssertableOutput output) {
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.untilAsserted(() -> assertThat(output)
				.hasSingleLineContaining("Message Received (@PulsarReader): Greeting[message=Hello from CRaC!]"));
	}

}
