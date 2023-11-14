package com.example.security.ldap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapClient;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.factory.PooledContextSource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.server.UnboundIdContainer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	UnboundIdContainer ldapContainer() throws Exception {
		UnboundIdContainer container = new UnboundIdContainer("dc=springframework,dc=org", "classpath:users.ldif");
		container.setPort(0);
		return container;
	}

	@Bean
	LdapContextSource contextSource(UnboundIdContainer container) {
		LdapContextSource ldap = new LdapContextSource();
		ldap.setUrl("ldap://localhost:" + container.getPort());
		ldap.setBase("dc=springframework,dc=org");
		return ldap;
	}

	@Bean
	PooledContextSource pooled(LdapContextSource ldap) {
		PooledContextSource pool = new PooledContextSource(new PoolConfig());
		pool.setContextSource(ldap);
		return pool;
	}

	@Bean
	LdapClient client(PooledContextSource clientContextSource) {
		return LdapClient.builder().contextSource(clientContextSource).build();
	}

	@Bean
	UserDetailsService users() {
		return new InMemoryUserDetailsManager(User.withUsername("user").password("{noop}password")
				.authorities("app").build());
	}

}
