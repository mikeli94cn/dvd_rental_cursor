package com.dvdrental.catalog.controller;

import com.dvdrental.catalog.dto.GenreRequest;
import com.dvdrental.catalog.dto.GenreResponse;
import com.dvdrental.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {

    private final CatalogService catalogService;

    public GenreController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<GenreResponse> list() {
        return catalogService.listGenres();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public GenreResponse create(@Valid @RequestBody GenreRequest request) {
        return catalogService.createGenre(request);
    }
}
