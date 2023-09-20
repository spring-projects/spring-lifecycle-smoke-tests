package com.example.contextrefresh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simple")
public class SimpleConfigurationProperties {

	private String test = "default";

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

}
