package com.example.loadbalancing.service;

import com.example.loadbalancing.client.DemoServiceClient;
import com.example.loadbalancing.client.TestServiceClient;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;

@Component
public class LoadBalancerClientTestService {

	private final TestServiceClient testServiceClient;

	private final DemoServiceClient demoServiceClient;

	public LoadBalancerClientTestService(TestServiceClient testServiceClient, DemoServiceClient demoServiceClient) {
		this.testServiceClient = testServiceClient;
		this.demoServiceClient = demoServiceClient;

	}

	public Flux<String> callServices() {
		return testServiceClient.test().concatWith(demoServiceClient.demo());
	}

}
