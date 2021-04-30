# OAuth2 Resource Server with custom granted authorities from JWT

This demo shows how to configure a Spring Boot application as an OAuth2 Resource Server using Spring Security,
and define a custom strategy to generate user authorities. Then, it uses the authorities to add authorization rules
for REST endpoints, both using the DSL and `@PreAuthorize`. It also shows how to write slice tests (`@WebMvcTest`)
and integration tests (`@SpringBootTest`, Wiremock, and Spring Cloud Contract) for the endpoints protected
by Spring Security.

By default, Spring Security generates a list of `GrantedAuthority` using the values in the `scope` or `scp` claim
and the `SCOPE_` prefix.

You can overwrite both prefix and source claim by defining a `JwtAuthenticationConverter` bean which gets
loaded by the `OAuth2ResourceServerConfigurer` automatically, without the need to pass it explicitly through the DSL.

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
        .build();
    }
```

## Generate GrantedAuthorities from a `roles` claim

The demo contains two `JwtAuthenticationConverter` examples. The first one (currently configured as a `@Bean`)
extracts the values from a custom `roles` claim and builds a list of `GrantedAuthority` using the `ROLE_` prefix.

```java
@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return jwtAuthenticationConverter;
}
```

## Generate GrantedAuthorities from a `realm_access.roles` claim (Keycloak)

The other example shows how to build a list of `GrantedAuthority` from the `realm_access` claim provided by Keycloak
in its access tokens and containing `roles` in a nested roles object.

```java
public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
        Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
        Collection<String> roles = realmAccess.get("roles");
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    };

    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return jwtAuthenticationConverter;
}
```