package com.thomasvitale.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class DemoApplicationTests {

	@Autowired
	WebTestClient webClient;

	@DynamicPropertySource
	@SuppressWarnings("unused")
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
				() -> "http://localhost:${wiremock.server.port}/protocol/openid-connect/certs");
	}

	@Test
	void whenGetBooksWithAuthorizedRoleThen200() {
		webClient
				.get().uri("/books")
				.headers(headers -> headers.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJja2I2MDlxOFhEOGJMcGVmMXdVeURoYXdhNFhXcl9GMno5WmN0OE9pRGVJIn0.eyJleHAiOjE2MTk4MTAwMDMsImlhdCI6MTYxOTc3NDA4NSwiYXV0aF90aW1lIjoxNjE5Nzc0MDAzLCJqdGkiOiIyNjY5MTkyNi03MTBmLTQ5MjMtOTExZS1lMjU5Yjk5Y2MzNTEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODEvYXV0aC9yZWFsbXMvU2VjdXJpdHlEZW1vIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImQ3YjFlMjg3LWI3MzUtNGQ1My05NWYxLTI4NzQ1ZDRiNjgxOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRlbW8tY2xpZW50Iiwibm9uY2UiOiJUSXNfbkRkZVd4Y2NUMFZmdWU5YThfWXJHWTlBQ1gxMm1CZGo2MEl1cFRFIiwic2Vzc2lvbl9zdGF0ZSI6IjY4YjIwZGE5LTdhYzYtNDdmZS1iMzE2LTgzODEyNGRlMjk4MyIsImFjciI6IjAiLCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsic3Vic2NyaWJlciIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2hlbGRvbi5jb29wZXIifQ.gLlAhW9RvgTGT8s85uBdKtsnWuWNKynLtKytimLXYxj9ROQTfHPZWaQ5zjhqWvqykHiBGqlbNdAXEDCof1HxJeObtYjUf-5oCA8Mop9g3xjwHowb81CqNAk1kIvkHIMZt6qacqdTF0fCY3BmElVrGMhu6fXUegvQXORQmc6GvSgI63UQUYidmxzWm2iUKh8dGMX-gGJm5P72gXB1SHpU8NVYz2pDkXLnOKagO0iyNeXeZHmVmEKfdCVg5_Ymi1Ud6RGeqhO0_qM26K6z6g4UQtITzZqnyfWusH2EGueTn98tAOxEqOz6zIrw3NunAl3ZVeqMQI1r9M3Xu6GWmh35CA"))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("The Lord of the Rings");
	}

	@Test
	void whenGetBooksWithoutAuthenticationThen401() {
		webClient
				.get().uri("/books")
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	void whenGetMoviesWithAuthorizedRoleThen200() {
		webClient
				.get().uri("/movies")
				.headers(headers -> headers.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJja2I2MDlxOFhEOGJMcGVmMXdVeURoYXdhNFhXcl9GMno5WmN0OE9pRGVJIn0.eyJleHAiOjE2MTk4MTAwMDMsImlhdCI6MTYxOTc3NDA4NSwiYXV0aF90aW1lIjoxNjE5Nzc0MDAzLCJqdGkiOiIyNjY5MTkyNi03MTBmLTQ5MjMtOTExZS1lMjU5Yjk5Y2MzNTEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODEvYXV0aC9yZWFsbXMvU2VjdXJpdHlEZW1vIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImQ3YjFlMjg3LWI3MzUtNGQ1My05NWYxLTI4NzQ1ZDRiNjgxOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRlbW8tY2xpZW50Iiwibm9uY2UiOiJUSXNfbkRkZVd4Y2NUMFZmdWU5YThfWXJHWTlBQ1gxMm1CZGo2MEl1cFRFIiwic2Vzc2lvbl9zdGF0ZSI6IjY4YjIwZGE5LTdhYzYtNDdmZS1iMzE2LTgzODEyNGRlMjk4MyIsImFjciI6IjAiLCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsic3Vic2NyaWJlciIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2hlbGRvbi5jb29wZXIifQ.gLlAhW9RvgTGT8s85uBdKtsnWuWNKynLtKytimLXYxj9ROQTfHPZWaQ5zjhqWvqykHiBGqlbNdAXEDCof1HxJeObtYjUf-5oCA8Mop9g3xjwHowb81CqNAk1kIvkHIMZt6qacqdTF0fCY3BmElVrGMhu6fXUegvQXORQmc6GvSgI63UQUYidmxzWm2iUKh8dGMX-gGJm5P72gXB1SHpU8NVYz2pDkXLnOKagO0iyNeXeZHmVmEKfdCVg5_Ymi1Ud6RGeqhO0_qM26K6z6g4UQtITzZqnyfWusH2EGueTn98tAOxEqOz6zIrw3NunAl3ZVeqMQI1r9M3Xu6GWmh35CA"))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.isEqualTo("Zack Snyder's Justice League");
	}

	@Test
	void whenGetMoviesWithoutAuthenticationThen401() {
		webClient
				.get().uri("/movies")
				.exchange()
				.expectStatus().isUnauthorized();
	}

}
