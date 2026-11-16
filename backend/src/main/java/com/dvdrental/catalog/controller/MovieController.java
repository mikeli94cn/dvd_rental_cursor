package com.dvdrental.catalog.controller;

import com.dvdrental.catalog.dto.MovieRequest;
import com.dvdrental.catalog.dto.MovieResponse;
import com.dvdrental.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final CatalogService catalogService;

    public MovieController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public Page<MovieResponse> search(@RequestParam(required = false) String q,
                                      @RequestParam(required = false) Long genreId,
                                      @PageableDefault(size = 20, sort = "title", direction = Sort.Direction.ASC)
                                      Pageable pageable) {
        return catalogService.searchMovies(q, genreId, pageable);
    }

    @GetMapping("/{id}")
    public MovieResponse get(@PathVariable Long id) {
        return catalogService.getMovie(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public MovieResponse create(@Valid @RequestBody MovieRequest request) {
        return catalogService.createMovie(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return catalogService.updateMovie(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        catalogService.deleteMovie(id);
    }
}
