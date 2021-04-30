package com.thomasvitale.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(DemoController.class)
@Import(SecurityConfig.class)
class DemoControllerTests {

	@Autowired
	WebTestClient webClient;

	@MockBean
	ReactiveJwtDecoder reactiveJwtDecoder;

	@Test
	void whenGetBooksWithAuthorizedRoleThen200() {
		webClient.mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_subscriber")))
				.get().uri("/books/")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("The Lord of the Rings");
	}

	@Test
	void whenGetBooksWithoutAuthorizedRoleThen403() {
		webClient.mutateWith(mockJwt())
				.get().uri("/books/")
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void whenGetBooksWithoutAuthenticationThen401() {
		webClient
				.get().uri("/books/")
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	void whenGetMoviesWithAuthorizedRoleThen200() {
		webClient.mutateWith(mockJwt().authorities(new SimpleGrantedAuthority("ROLE_subscriber")))
				.get().uri("/movies")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("Zack Snyder's Justice League");
	}

	@Test
	void whenGetMoviesWithoutAuthorizedRoleThen403() {
		webClient.mutateWith(mockJwt())
				.get().uri("/movies")
				.exchange()
				.expectStatus().isForbidden();
	}

	@Test
	void whenGetMoviesWithoutAuthenticationThen401() {
		webClient
				.get().uri("/movies")
				.exchange()
				.expectStatus().isUnauthorized();
	}

}