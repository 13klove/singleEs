package com.es.sample.demo.movie.service;


import com.es.sample.demo.movie.repository.MovieEsRepository;
import com.es.sample.demo.movie.repository.MovieJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieJpaRepository movieJpaRepository;
    private final MovieEsRepository movieEsRepository;

    public void aa(){
        movieEsRepository.saveAll(null);
    }

}
