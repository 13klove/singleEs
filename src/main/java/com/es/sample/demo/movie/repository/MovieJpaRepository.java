package com.es.sample.demo.movie.repository;

import com.es.sample.demo.movie.model.entity.Movie;
import com.es.sample.demo.movie.repository.queryDsl.dto.MovieDtoDslRepository;
import com.es.sample.demo.movie.repository.queryDsl.entity.MovieDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieJpaRepository extends JpaRepository<Movie, Long>, MovieDslRepository, MovieDtoDslRepository {



}
