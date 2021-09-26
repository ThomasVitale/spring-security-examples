package com.thomasvitale.demo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	@GetMapping("user")
	public Mono<OidcUser> getUser(@AuthenticationPrincipal OidcUser oidcUser) {
		return Mono.just(oidcUser);
	}

	@GetMapping("books")
	@PreAuthorize("hasRole('subscriber')")
	public Flux<String> getBooks(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
		System.out.println(client.getAccessToken().getTokenValue());
		return Flux.just("The Lord of the Rings");
	}

	@GetMapping("movies")
	public Flux<String> getMovies() {
		return Flux.just("Zack Snyder's Justice League");
	}

}
