package com.example.resttemplate;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
class RestTemplateConfiguration {

	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder, ClientHttpRequestFactory requestFactory) {
		return builder.defaultHeader("User-Agent", "RestTemplateApplication")
			.requestFactory(() -> requestFactory)
			.build();
	}

	@Bean
	ReactorResourceFactory resourceFactory() {
		return new ReactorResourceFactory();
	}

	@Bean
	ClientHttpRequestFactory requestFactory(ReactorResourceFactory resourceFactory) {
		return new ReactorNettyClientRequestFactory(resourceFactory, mapper -> mapper.compress(true));
	}

}
