package com.thomasvitale.demo;

import reactor.core.publisher.Flux;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	@GetMapping("books")
	@PreAuthorize("hasRole('subscriber')")
	public Flux<String> getBooks() {
		return Flux.just("The Lord of the Rings");
	}

	@GetMapping("movies")
	public Flux<String> getMovies() {
		return Flux.just("Zack Snyder's Justice League");
	}

}
