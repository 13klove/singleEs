package com.es.sample.demo.movie.model.es;

import com.es.sample.demo.actor.model.es.Actor;
import com.es.sample.demo.movie.status.Country;
import com.es.sample.demo.movie.status.Genre;
import com.es.sample.demo.score.model.es.Score;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "movie")
public class Movie {

    @Id
    private Long movieId;

    private String movieName;

    private String movieEnName;

    //날짜 문제 확인해야 한다.

    //@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy.MM.dd")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    //private LocalDate openingDate;
    private String openingDate;

    private List<Genre> genre;

    private Country country;

    private Integer age;

    private List<Actor> actors;

    private List<Score> scores;

}
