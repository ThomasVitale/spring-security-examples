package com.thomasvitale.demo;

import java.util.Collection;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	@GetMapping("books")
	@PreAuthorize("hasRole('subscriber')")
	public Collection<String> getBooks() {
		return List.of("The Lord of the Rings");
	}

	@GetMapping("movies")
	public Collection<String> getMovies() {
		return List.of("Zack Snyder's Justice League");
	}

}
