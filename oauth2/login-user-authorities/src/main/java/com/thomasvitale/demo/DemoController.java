package com.thomasvitale.demo;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	private static final Logger log = LoggerFactory.getLogger(DemoController.class);

	@GetMapping("user")
	public OidcUser getUser(@AuthenticationPrincipal OidcUser oidcUser) {
		return oidcUser;
	}

	@GetMapping("authorities")
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	}

	@GetMapping("books")
	@PreAuthorize("hasRole('subscriber')")
	public Collection<String> getBooks(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
		log.info("OAuth2 Access Token: {}", client.getAccessToken().getTokenValue());
		return List.of("The Lord of the Rings");
	}

	@GetMapping("movies")
	public Collection<String> getMovies() {
		return List.of("Zack Snyder's Justice League");
	}

}
