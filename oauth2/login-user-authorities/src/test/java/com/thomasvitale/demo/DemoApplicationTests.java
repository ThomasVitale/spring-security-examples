package com.thomasvitale.demo;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@SpringBootTest
class DemoApplicationTests {

	@MockBean // Skip calling the Issuer URI
	ClientRegistrationRepository clientRegistrationRepository;

	@Test
	void contextLoads() {
		// Spring context loads correctly
	}

}
