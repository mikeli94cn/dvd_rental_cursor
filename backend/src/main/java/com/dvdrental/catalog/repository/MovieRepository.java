package com.dvdrental.catalog.repository;

import com.dvdrental.catalog.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
            SELECT DISTINCT m FROM Movie m
            LEFT JOIN m.genres g
            WHERE (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:genreId IS NULL OR g.id = :genreId)
            """)
    Page<Movie> search(@Param("q") String q, @Param("genreId") Long genreId, Pageable pageable);
}
