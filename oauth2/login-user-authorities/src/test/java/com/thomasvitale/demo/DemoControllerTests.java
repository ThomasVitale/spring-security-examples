package com.thomasvitale.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DemoController.class)
@Import(SecurityConfig.class)
class DemoControllerTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	@MockBean
	ClientRegistrationRepository clientRegistrationRepository;

	@Test
	void whenGetUserWithAuthorizedRoleThen200() throws Exception {
		mockMvc
				.perform(get("/user")
						.with(oidcLogin()))
				.andExpect(status().isOk());
	}

	@Test
	void whenGetUserWithoutAuthenticationThen302() throws Exception {
		mockMvc
				.perform(get("/user"))
				.andExpect(status().isFound());
	}

	@Test
	void whenGetBooksWithAuthorizedRoleThen200() throws Exception {
		mockMvc
				.perform(get("/books")
						.with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_subscriber"))))
				.andExpect(status().isOk());
	}

	@Test
	void whenGetBooksWithoutAuthorizedRoleThen403() throws Exception {
		mockMvc
				.perform(get("/books")
						.with(oidcLogin()))
				.andExpect(status().isForbidden());
	}

	@Test
	void whenGetBooksWithoutAuthenticationThen302() throws Exception {
		mockMvc
				.perform(get("/books"))
				.andExpect(status().isFound());
	}

	@Test
	void whenGetMoviesWithAuthorizedRoleThen200() throws Exception {
		mockMvc
				.perform(get("/movies")
						.with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_subscriber"))))
				.andExpect(status().isOk());
	}

	@Test
	void whenGetMoviesWithoutAuthorizedRoleThen403() throws Exception {
		mockMvc
				.perform(get("/movies")
						.with(oidcLogin()))
				.andExpect(status().isForbidden());
	}

	@Test
	void whenGetMoviesWithoutAuthenticationThen302() throws Exception {
		mockMvc
				.perform(get("/movies"))
				.andExpect(status().isFound());
	}

}