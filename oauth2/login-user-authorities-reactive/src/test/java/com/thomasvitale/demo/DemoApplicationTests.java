package com.thomasvitale.demo;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

@SpringBootTest
class DemoApplicationTests {

	@MockBean // Skip calling the Issuer URI
	ReactiveClientRegistrationRepository clientRegistrationRepository;

	@Test
	void contextLoads() {
		// Spring context loads correctly
	}

}
