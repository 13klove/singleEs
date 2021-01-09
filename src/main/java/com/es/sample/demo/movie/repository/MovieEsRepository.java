package com.es.sample.demo.movie.repository;


import com.es.sample.demo.movie.model.es.Movie;
import com.es.sample.demo.movie.status.Genre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MovieEsRepository extends ElasticsearchRepository<Movie, Long> {

    List<Movie> findMoviesByGenre(Genre genre);

    SearchHits<Movie> findMovieByGenre(Genre genre);

    SearchHits<Movie> findMoviesByMovieEnName(String movieEnName);

    List<Movie> findMovieByGenre(Genre genre, Pageable pageable);

    List<Movie> findMovieByGenreOrderByMovieNameAsc(Genre genre, Pageable pageable);

    List<Movie> findMovieByGenreOrderByAgeAsc(Genre genre, Pageable pageable);

}
