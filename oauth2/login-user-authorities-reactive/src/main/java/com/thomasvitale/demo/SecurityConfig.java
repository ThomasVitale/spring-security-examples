package com.thomasvitale.demo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

	private static final String ROLES_CLAIM = "roles";

	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		return http
				.authorizeExchange(exchange -> exchange
						.pathMatchers("/movies").hasRole("subscriber")
						.anyExchange().authenticated()
				)
				.oauth2Login(Customizer.withDefaults())
				.build();
	}

	@Bean
	@SuppressWarnings({"unchecked", "java:S5411"})
	GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return authorities -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			var authority = authorities.iterator().next();
			boolean isOidc = authority instanceof OidcUserAuthority;

			if (isOidc) {
				var oidcUserAuthority = (OidcUserAuthority) authority;
				var userInfo = oidcUserAuthority.getUserInfo();

				if (userInfo.containsClaim(ROLES_CLAIM)) {
					var roles = userInfo.getClaimAsStringList(ROLES_CLAIM);
					mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
				}
			} else {
				var oauth2UserAuthority = (OAuth2UserAuthority) authority;
				Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

				if (userAttributes.containsKey(ROLES_CLAIM)) {
					var roles =  (Collection<String>) userAttributes.get(ROLES_CLAIM);
					mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
				}
			}

			return mappedAuthorities;
		};
	}

	Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
		return roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toList());
	}

}
