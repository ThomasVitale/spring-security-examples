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

	private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJxcmhVVjZPeGZFbURWY3dhZTZCaThWaFk0NTZ6LWpJWUppb25pVXh6N213In0.eyJleHAiOjE3MDYzNTU2MDgsImlhdCI6MTYxOTk1NTY3NiwiYXV0aF90aW1lIjoxNjE5OTU1Njc2LCJqdGkiOiJkYzY5MDdhOS02YzE5LTQ1NjQtODQ3Ny1hZjhiZjc4NTFjNjYiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODEvYXV0aC9yZWFsbXMvU2VjdXJpdHlEZW1vIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjIxMGEwZmViLTg2MjEtNGEyNi1iYTE5LWE4ZDBhZDJmOWE3MyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRlbW8tY2xpZW50Iiwibm9uY2UiOiJVNnlnbk95U09vQ3B1dmQtN081UW82bEE2MG8xQ29vQXZsdjhRZ3BRdUo4Iiwic2Vzc2lvbl9zdGF0ZSI6IjNmYjcyZTVkLWRmNjktNDc0NC1hZGYxLTE4ZDNhNmQxMWFiYyIsImFjciI6IjEiLCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJyb2xlcyI6WyJzdWJzY3JpYmVyIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdLCJuYW1lIjoiU2hlbGRvbiBDb29wZXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzaGVsZG9uLmNvb3BlciIsImdpdmVuX25hbWUiOiJTaGVsZG9uIiwiZmFtaWx5X25hbWUiOiJDb29wZXIifQ.cHzMHKYoHcDrCm5s3rfBD2bDuvHYrc_exdanTqpCM29K7D1EKAwMf0D1gm1_hIUFIKUz5ZTV067MlKsh5IzAI0iFzB2sCPQZM66bSRpMUJ--OdIPnFWMd6EaQN5ZvvtxiiIlrMamZdsrH8xQNyFf164b7QjHWZkcpd3mA81dWKBYgvUoBCOheW1M3wRpPiH6JvvlMmatYOerJoWOE6bCyCYVlvt2OXpCrHqiKVuGc7-ZFh2qg2hIgY1QLQVnnvDxXvA8e55pKXVsZDNxtqDsvDrgoqI2i6FIuNp1_IQ_kvxVdcWJ2w2zBPIBS7_a_cBXG_3FAhySn7XQDfCjQMwl4g";

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
