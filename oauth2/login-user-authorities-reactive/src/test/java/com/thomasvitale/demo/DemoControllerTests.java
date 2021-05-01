package com.thomasvitale.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;

@WebFluxTest(DemoController.class)
@Import(SecurityConfig.class)
class DemoControllerTests {

	@Autowired
	WebTestClient webClient;

	@MockBean
	ReactiveClientRegistrationRepository clientRegistrationRepository;

	@Test
	void whenGetUserWithAuthorizedRoleThen200() {
		webClient.mutateWith(mockOidcLogin())
				.get().uri("/user")
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	void whenGetUserWithoutAuthenticationThen302() {
		webClient
				.get().uri("/user")
				.exchange()
				.expectStatus().isFound();
	}

	@Test
	void whenGetBooksWithAuthorizedRoleThen200() {
		webClient.mutateWith(mockOidcLogin().authorities(new SimpleGrantedAuthority("ROLE_subscriber")))
				.get().uri("/books")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("The Lord of the Rings");
	}

	@Test
	void whenGetBooksWithoutAuthorizedRoleThen403() {
		webClient.mutateWith(mockOidcLogin())
				.get().uri("/books")
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void whenGetBooksWithoutAuthenticationThen302() {
		webClient
				.get().uri("/books")
				.exchange()
				.expectStatus().isFound();
	}

	@Test
	void whenGetMoviesWithAuthorizedRoleThen200() {
		webClient.mutateWith(mockOidcLogin().authorities(new SimpleGrantedAuthority("ROLE_subscriber")))
				.get().uri("/movies")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("Zack Snyder's Justice League");
	}

	@Test
	void whenGetMoviesWithoutAuthorizedRoleThen403() {
		webClient.mutateWith(mockOidcLogin())
				.get().uri("/movies")
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void whenGetMoviesWithoutAuthenticationThen302() {
		webClient
				.get().uri("/movies")
				.exchange()
				.expectStatus().isFound();
	}

}