package com.thomasvitale.demo;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class DemoApplicationTests {

	private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJja2I2MDlxOFhEOGJMcGVmMXdVeURoYXdhNFhXcl9GMno5WmN0OE9pRGVJIn0.eyJleHAiOjE2MTk4MTAwMDMsImlhdCI6MTYxOTc3NDA4NSwiYXV0aF90aW1lIjoxNjE5Nzc0MDAzLCJqdGkiOiIyNjY5MTkyNi03MTBmLTQ5MjMtOTExZS1lMjU5Yjk5Y2MzNTEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODEvYXV0aC9yZWFsbXMvU2VjdXJpdHlEZW1vIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImQ3YjFlMjg3LWI3MzUtNGQ1My05NWYxLTI4NzQ1ZDRiNjgxOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRlbW8tY2xpZW50Iiwibm9uY2UiOiJUSXNfbkRkZVd4Y2NUMFZmdWU5YThfWXJHWTlBQ1gxMm1CZGo2MEl1cFRFIiwic2Vzc2lvbl9zdGF0ZSI6IjY4YjIwZGE5LTdhYzYtNDdmZS1iMzE2LTgzODEyNGRlMjk4MyIsImFjciI6IjAiLCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsic3Vic2NyaWJlciIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2hlbGRvbi5jb29wZXIifQ.gLlAhW9RvgTGT8s85uBdKtsnWuWNKynLtKytimLXYxj9ROQTfHPZWaQ5zjhqWvqykHiBGqlbNdAXEDCof1HxJeObtYjUf-5oCA8Mop9g3xjwHowb81CqNAk1kIvkHIMZt6qacqdTF0fCY3BmElVrGMhu6fXUegvQXORQmc6GvSgI63UQUYidmxzWm2iUKh8dGMX-gGJm5P72gXB1SHpU8NVYz2pDkXLnOKagO0iyNeXeZHmVmEKfdCVg5_Ymi1Ud6RGeqhO0_qM26K6z6g4UQtITzZqnyfWusH2EGueTn98tAOxEqOz6zIrw3NunAl3ZVeqMQI1r9M3Xu6GWmh35CA";

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	TestRestTemplate restTemplate;

	@DynamicPropertySource
	@SuppressWarnings("unused")
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
				() -> "http://localhost:${wiremock.server.port}/protocol/openid-connect/certs");
	}

	@Test
	void whenGetBooksWithAuthorizedRoleThen200() {
		ResponseEntity<List<String>> response = restTemplate
				.exchange("/books", HttpMethod.GET, new HttpEntity<>(getAuthorizationBearerHeader()), new ParameterizedTypeReference<>() {});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody())
				.contains("The Lord of the Rings");
	}

	@Test
	void whenGetBooksWithoutAuthenticationThen401() {
		ResponseEntity<List<String>> response = restTemplate
				.exchange("/books", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void whenGetMoviesWithAuthorizedRoleThen200() {
		ResponseEntity<List<String>> response = restTemplate
				.exchange("/movies", HttpMethod.GET, new HttpEntity<>(getAuthorizationBearerHeader()), new ParameterizedTypeReference<>() {});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody())
				.contains("Zack Snyder's Justice League");
	}

	@Test
	void whenGetMoviesWithoutAuthenticationThen401() {
		ResponseEntity<List<String>> response = restTemplate
				.exchange("/movies", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private HttpHeaders getAuthorizationBearerHeader() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(ACCESS_TOKEN);
		return httpHeaders;
	}
}
