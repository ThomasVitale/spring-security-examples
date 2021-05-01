# OAuth2 Login with custom granted authorities from UserInfo (Reactive)

This demo shows how to configure a Spring Boot application as an OAuth2 Client with OpenID Connect authentication
using reactive Spring Security, and define a custom strategy to generate user authorities from UserInfo.
Then, it uses the authorities to add authorization rules for REST endpoints, both using the DSL and `@PreAuthorize`.
It also shows how to write slice tests (`@WebFluxTest`) and integration tests (`@SpringBootTest`, Wiremock,
and Spring Cloud Contract) for the endpoints protected by Spring Security. 

By default, Spring Security generates a list of `GrantedAuthority` using the values in the `scope` or `scp` claim
and the `SCOPE_` prefix.

You can overwrite both prefix and source claim by either defining a `GrantedAuthoritiesMapper` bean or
`OAuth2UserService` bean.

```java
@Bean
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
```
