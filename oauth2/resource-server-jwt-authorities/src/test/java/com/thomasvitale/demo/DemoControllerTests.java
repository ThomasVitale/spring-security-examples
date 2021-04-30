package com.thomasvitale.demo;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DemoController.class)
@Import(SecurityConfig.class)
class DemoControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	JwtDecoder jwtDecoder;

	@Test
	void whenGetBooksWithAuthorizedRoleThen200() throws Exception {
		mockMvc
				.perform(get("/books")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_subscriber"))))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(List.of("The Lord of the Rings"))));
	}

	@Test
	void whenGetBooksWithoutAuthorizedRoleThen403() throws Exception {
		mockMvc
				.perform(get("/books")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_visitor"))))
				.andExpect(status().isForbidden());
	}

	@Test
	void whenGetBooksWithoutAuthenticationThen401() throws Exception {
		mockMvc
				.perform(get("/books"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void whenGetMoviesWithAuthorizedRoleThen200() throws Exception {
		mockMvc
				.perform(get("/movies")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_subscriber"))))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(List.of("Zack Snyder's Justice League"))));
	}

	@Test
	void whenGetMoviesWithoutAuthorizedRoleThen403() throws Exception {
		mockMvc
				.perform(get("/movies")
						.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_visitor"))))
				.andExpect(status().isForbidden());
	}

	@Test
	void whenGetMoviesWithoutAuthenticationThen401() throws Exception {
		mockMvc
				.perform(get("/movies"))
				.andExpect(status().isUnauthorized());
	}

}