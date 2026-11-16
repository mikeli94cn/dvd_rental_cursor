package com.dvdrental.catalog.service;

import com.dvdrental.catalog.dto.GenreRequest;
import com.dvdrental.catalog.dto.GenreResponse;
import com.dvdrental.catalog.dto.MovieRequest;
import com.dvdrental.catalog.dto.MovieResponse;
import com.dvdrental.catalog.entity.Genre;
import com.dvdrental.catalog.entity.Movie;
import com.dvdrental.catalog.repository.GenreRepository;
import com.dvdrental.catalog.repository.MovieRepository;
import com.dvdrental.common.error.ApiException;
import com.dvdrental.inventory.entity.DvdCopyStatus;
import com.dvdrental.inventory.repository.DvdCopyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CatalogService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final DvdCopyRepository dvdCopyRepository;

    public CatalogService(MovieRepository movieRepository,
                          GenreRepository genreRepository,
                          DvdCopyRepository dvdCopyRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.dvdCopyRepository = dvdCopyRepository;
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> searchMovies(String q, Long genreId, Pageable pageable) {
        String query = (q == null || q.isBlank()) ? null : q.trim();
        return movieRepository.search(query, genreId, pageable).map(this::toMovieResponse);
    }

    @Transactional(readOnly = true)
    public MovieResponse getMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Movie not found"));
        return toMovieResponse(movie);
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = new Movie();
        applyMovie(movie, request);
        return toMovieResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Movie not found"));
        applyMovie(movie, request);
        return toMovieResponse(movieRepository.save(movie));
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw ApiException.notFound("Movie not found");
        }
        movieRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GenreResponse> listGenres() {
        return genreRepository.findAll().stream()
                .map(g -> new GenreResponse(g.getId(), g.getName()))
                .toList();
    }

    @Transactional
    public GenreResponse createGenre(GenreRequest request) {
        if (genreRepository.existsByNameIgnoreCase(request.name())) {
            throw ApiException.conflict("Genre already exists");
        }
        Genre genre = new Genre();
        genre.setName(request.name().trim());
        genre = genreRepository.save(genre);
        return new GenreResponse(genre.getId(), genre.getName());
    }

    private void applyMovie(Movie movie, MovieRequest request) {
        movie.setTitle(request.title().trim());
        movie.setDescription(request.description());
        movie.setReleaseYear(request.releaseYear());
        movie.setDailyRate(request.dailyRate());
        movie.setRentalDaysDefault(request.rentalDaysDefault() == null ? 3 : request.rentalDaysDefault());
        Set<Genre> genres = new HashSet<>();
        if (request.genreIds() != null) {
            for (Long genreId : request.genreIds()) {
                Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> ApiException.badRequest("Unknown genre id: " + genreId));
                genres.add(genre);
            }
        }
        movie.setGenres(genres);
    }

    private MovieResponse toMovieResponse(Movie movie) {
        long available = dvdCopyRepository.countByMovieIdAndStatus(movie.getId(), DvdCopyStatus.AVAILABLE);
        List<GenreResponse> genres = movie.getGenres().stream()
                .map(g -> new GenreResponse(g.getId(), g.getName()))
                .toList();
        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getReleaseYear(),
                movie.getDailyRate(),
                movie.getRentalDaysDefault(),
                genres,
                available
        );
    }
}
