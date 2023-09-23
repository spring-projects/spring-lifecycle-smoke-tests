package com.example.pulsar.reactive;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import org.springframework.cr.smoketest.support.assertj.AssertableOutput;
import org.springframework.cr.smoketest.support.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationTest
public class SpringPulsarApplicationTests {

	@Test
	void reactivePulsarTemplateSendsMessage(AssertableOutput output) {
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.untilAsserted(() -> assertThat(output)
				.hasSingleLineContaining("Message Sent (ReactivePulsarTemplate): Greeting[message=Hello from CRaC!]"));
	}

	@Test
	void reactivePulsarListenerMethodReceivesMessage(AssertableOutput output) {
		Awaitility.await()
			.atMost(Duration.ofSeconds(30))
			.untilAsserted(() -> assertThat(output).hasSingleLineContaining(
					"Message Received (@ReactivePulsarListener): Greeting[message=Hello from CRaC!]"));
	}

}
