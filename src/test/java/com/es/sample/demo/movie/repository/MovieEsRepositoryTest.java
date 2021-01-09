package com.es.sample.demo.movie.repository;

import com.es.sample.demo.actor.model.es.Actor;
import com.es.sample.demo.actor.status.Gender;
import com.es.sample.demo.movie.model.es.Movie;
import com.es.sample.demo.movie.status.Country;
import com.es.sample.demo.movie.status.Genre;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

@SpringBootTest
public class MovieEsRepositoryTest {

    @Autowired
    MovieEsRepository movieEsRepository;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void movieEsRepositoryInsertTest(){
        Movie movie = Movie.builder()
                .movieId(2l)
                .movieName("타이타닉")
                .movieEnName("Titanic")
                //.openingDate(LocalDate.parse("19980220", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .openingDate("1998-02-20")
                .genre(asList(Genre.MELLOW, Genre.ROMANCE, Genre.DRAMA))
                .country(Country.USA)
                .age(15)
                .actors(asList(Actor.builder().name("레오나르도 디카프리오").gender(Gender.MAN).build(), Actor.builder().name("케이트 윈슬렛").gender(Gender.WOMAN).build()))
                .build();
        movieEsRepository.save(movie);
    }

    @Test
    public void elasticsearchRestTemplateInsertTest(){
        Movie movie = Movie.builder()
                .movieId(3l)
                .movieName("인셉션")
                .movieEnName("Inception")
                //.openingDate(LocalDate.parse("19980220", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .openingDate("2010-07-21")
                .genre(asList(Genre.SF, Genre.ACTION, Genre.THRILLER, Genre.ADVENTURE))
                .country(Country.USA)
                .age(12)
                .actors(asList(Actor.builder().name("레오나르도 디카프리오").gender(Gender.MAN).build(), Actor.builder().name("와타나베 켄").gender(Gender.MAN).build()
                        , Actor.builder().name("조셉 고든 레빗").gender(Gender.MAN).build(), Actor.builder().name("톰하디").gender(Gender.MAN).build()
                        , Actor.builder().name("마리옹 꼬띠아르").gender(Gender.WOMAN).build(), Actor.builder().name("엘렌 페이지").gender(Gender.WOMAN).build()))
                .build();

        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(movie.getMovieId().toString())
                .withObject(movie)
                .build();

        String documentId = elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of("movie"));
        System.out.println(documentId);
        Assertions.assertNotNull(documentId);
    }

    @Test
    public void elasticsearchRestTemplateBulkInsertTest(){
        Movie movie = Movie.builder()
                .movieId(4l)
                .movieName("라라랜드")
                .movieEnName("La La Land")
                //.openingDate(LocalDate.parse("19980220", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .openingDate("2016-12-07")
                .genre(asList(Genre.DRAMA, Genre.MUSICAL, Genre.MELLOW, Genre.ROMANCE))
                .country(Country.USA)
                .age(12)
                .actors(asList(Actor.builder().name("라이언 고슬링").gender(Gender.MAN).build(), Actor.builder().name("엠마 스톤").gender(Gender.WOMAN).build()))
                .build();

        Movie movie2 = Movie.builder()
                .movieId(5l)
                .movieName("라이언 일병 구하기")
                .movieEnName("Saving Private Ryan")
                //.openingDate(LocalDate.parse("19980220", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .openingDate("1998-09-12")
                .genre(asList(Genre.DRAMA, Genre.WAR, Genre.ACTION))
                .country(Country.USA)
                .age(15)
                .actors(asList(Actor.builder().name("톰 행크스").gender(Gender.MAN).build(), Actor.builder().name("에드워드 번즈").gender(Gender.MAN).build()
                        , Actor.builder().name("톰 시즈모어").gender(Gender.MAN).build(), Actor.builder().name("제레미 데이비스").gender(Gender.MAN).build()
                        , Actor.builder().name("빈 디젤").gender(Gender.MAN).build(), Actor.builder().name("아담 골드버그").gender(Gender.MAN).build()
                        , Actor.builder().name("래리 페퍼").gender(Gender.MAN).build(), Actor.builder().name("지오바니 리비시").gender(Gender.MAN).build()
                        , Actor.builder().name("맷 데이먼").gender(Gender.MAN).build(), Actor.builder().name("데니스 파리나").gender(Gender.MAN).build()
                        , Actor.builder().name("테드 댄슨").gender(Gender.MAN).build()))
                .build();

        Movie movie3 = Movie.builder()
                .movieId(6l)
                .movieName("달콤한 인생")
                .movieEnName("A Bittersweet Life")
                //.openingDate(LocalDate.parse("19980220", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .openingDate("2005-04-01")
                .genre(asList(Genre.DRAMA, Genre.NORI, Genre.ACTION))
                .country(Country.KO)
                .age(19)
                .actors(asList(Actor.builder().name("이병헌").gender(Gender.MAN).build(), Actor.builder().name("김영철").gender(Gender.MAN).build()
                        , Actor.builder().name("신민아").gender(Gender.WOMAN).build()))
                .build();

        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(movie.getMovieId().toString())
                .withObject(movie)
                .build();

        IndexQuery indexQuery2 = new IndexQueryBuilder()
                .withId(movie2.getMovieId().toString())
                .withObject(movie2)
                .build();

        IndexQuery indexQuery3 = new IndexQueryBuilder()
                .withId(movie3.getMovieId().toString())
                .withObject(movie3)
                .build();
        List<IndexQuery> query = asList(indexQuery, indexQuery2, indexQuery3);

        List<String> documentIds = elasticsearchRestTemplate.bulkIndex(query, IndexCoordinates.of("movie"));
        System.out.println(documentIds);
        Assertions.assertNotEquals(documentIds.size(), 0);
    }

    @Test
    public void readFileBulkMovie(){
        Gson gson = new Gson();
        try {
            List<Movie> movies = gson.fromJson(new JsonReader(new FileReader("C:\\Users\\hbjang\\elasticsearchProjects\\elasticsearchProject\\api\\src\\main\\resources\\sample_data.json")), new TypeToken<List<Movie>>(){}.getType());
            AtomicLong count = new AtomicLong();
            List<IndexQuery> collect = movies.stream().map(a -> {
                a.setMovieId(count.getAndAdd(1));
                return new IndexQueryBuilder()
                        .withId(a.getMovieId().toString())
                        .withObject(a)
                        .build();
            }).collect(Collectors.toList());
            List<String> docs = elasticsearchRestTemplate.bulkIndex(collect, IndexCoordinates.of("movie"));
            System.out.println(docs);
            Assertions.assertNotEquals(docs.size(), 0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
