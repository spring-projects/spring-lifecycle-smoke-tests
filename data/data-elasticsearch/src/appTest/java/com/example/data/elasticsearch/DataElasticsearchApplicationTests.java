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

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.cr.smoketest.support.assertj.AssertableOutput;
import org.springframework.cr.smoketest.support.junit.ApplicationTest;

@ApplicationTest
public class DataElasticsearchApplicationTests {

	@Test
	void connectionTest(AssertableOutput output) {
		Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
			assertThat(output).hasLineMatching("Starting ElasticsearchReader: was (initialized|stopped)");
			assertThat(output).hasLineContaining("You Know, for Search");
		});
	}

}
