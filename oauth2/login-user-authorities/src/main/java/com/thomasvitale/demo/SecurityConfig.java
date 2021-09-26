package com.thomasvitale.demo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private static final String REALM_ACCESS_CLAIM = "realm_access";
	private static final String ROLES_CLAIM = "roles";

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeRequests(exchange -> exchange
						.mvcMatchers("/movies").hasRole("subscriber")
						.anyRequest().authenticated()
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

				if (userInfo.hasClaim(ROLES_CLAIM)) {
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

	//@Bean uncomment when using Keycloak realm roles
	@SuppressWarnings({"unchecked", "java:S5411"})
	public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
		return authorities -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			var authority = authorities.iterator().next();
			boolean isOidc = authority instanceof OidcUserAuthority;

			if (isOidc) {
				var oidcUserAuthority = (OidcUserAuthority) authority;
				var userInfo = oidcUserAuthority.getUserInfo();

				if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
					var realmAccess = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM);
					var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
					mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
				}
			} else {
				var oauth2UserAuthority = (OAuth2UserAuthority) authority;
				Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

				if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
					var realmAccess =  (Map<String,Object>) userAttributes.get(REALM_ACCESS_CLAIM);
					var roles =  (Collection<String>) realmAccess.get(ROLES_CLAIM);
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
