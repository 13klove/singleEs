package com.es.sample.demo.movie.repository;


import com.es.sample.demo.actor.status.Gender;
import com.es.sample.demo.movie.model.es.Movie;
import com.es.sample.demo.movie.status.Genre;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
public class MovieEsRepositoryInsertTest {

    @Autowired
    MovieEsRepository movieEsRepository;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    RestHighLevelClient elasticsearchClient;

    //검색에는 쿼리 컨텍스트, 필터 컨텍스트 2가지가 있다.
    //속도면에서 필터가 빠르기에 쿼리와 필터를 잘 섞어서 사용해야 한다.
    //무조건 쿼리 컨텍스트로 분석할 필요가 없다.

    //repository같은 경우 멀티 인덱스 조회, operator, minimum_should_match, fuzziness, boost등 여러 옵션 사용이 불가능하다.
    //추가로 정렬도 경우에 따라 안된다. movieName은 text와 keyword 2가지 타입이나 repository에서는 서브타입 선택이 불가능해 보인다.
    //즉 구체적인 쿼리를 작성하기 위해서는 elasticsearchRestTemplate 혹은 es에서 제공하는 라이브러리를 사용해아 한다.
    @Test
    public void 리파지토리로_키_조회(){
        Optional<Movie> byId = movieEsRepository.findById(3l);
        System.out.println(byId.get());
        Assertions.assertNotNull(byId.get());
    }

    @Test
    public void 레스트템플로_키_조회(){
        Movie movie = elasticsearchRestTemplate.get("3", Movie.class);
        System.out.println(movie);
        Assertions.assertNotNull(movie);
    }

    @Test
    public void 리파지토리로_장르_조회(){
        //흠... 리파지토리로 하면 filter는 적용이 안되는거 같다. 오직 query만 가능한거 같다.
        List<Movie> moviesByGenre = movieEsRepository.findMoviesByGenre(Genre.ACTION);
        System.out.println(moviesByGenre);
        Assertions.assertNotEquals(moviesByGenre.size(), 0);

        SearchHits<Movie> jurassic = movieEsRepository.findMoviesByMovieEnName("Jurassic");
        System.out.println(jurassic);

        SearchHits<Movie> jurassics = movieEsRepository.findMovieByGenre(Genre.ACTION);
        System.out.println(jurassics);
    }

    @Test
    public void 레스트템플로_장르_조회(){
        //7버전 부터는 filter는 bool 안에서 사용해야 하는거 같다.
        //SearchRequest searchRequest = new NativeSearchQueryBuilder();
        NativeSearchQuery genre = new NativeSearchQueryBuilder()
                //.withQuery(QueryBuilders.termsQuery("genre", Genre.THRILLER))
                //.withQuery(QueryBuilders.matchQuery("movieEnName", "Constantine"))
                //.withQuery(QueryBuilders.matchQuery("movieEnName", "Jurassic"))
                //.withQuery(QueryBuilders.matchQuery("movieEnName", "Jurassic World"))
                //.withFilter(QueryBuilders.termsQuery("genre", Genre.THRILLER))
                //.withFilter(QueryBuilders.matchQuery("movieEnName", "Jurassic World"))
                .withFilter(QueryBuilders.termsQuery("movieEnName", "Jurassic"))
                .build();
        SearchHits<Movie> search = elasticsearchRestTemplate.search(genre, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a));
        List<Movie> collect = search.get().map(a -> a.getContent()).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void 리파지토리로_페이징정렬_조회(){
        List<Movie> movieByGenre = movieEsRepository.findMovieByGenre(Genre.ACTION, PageRequest.of(0, 3));
        System.out.println(movieByGenre.size());
        movieByGenre.forEach(System.out::println);

        //List<Movie> movieByGenres = movieEsRepository.findMovieByGenreOrderByMovieNameAsc(Genre.ACTION, PageRequest.of(0, 3));
        List<Movie> movieByGenres = movieEsRepository.findMovieByGenreOrderByAgeAsc(Genre.ACTION, PageRequest.of(0, 3));
        System.out.println(movieByGenres.size());
        movieByGenres.forEach(System.out::println);
    }

    @Test
    public void 레스트템플로_페이징정렬_조회(){
        NativeSearchQuery movieName = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(0, 3))
                .withSort(SortBuilders.fieldSort("movieName.keyword").order(SortOrder.DESC))
                .withQuery(QueryBuilders.termQuery("genre", Genre.ACTION))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(movieName, Movie.class);
        search.get().forEach(a-> System.out.println(a));
        List<Movie> collect = search.get().map(a -> a.getContent()).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void 레스트템플로_필드범위_조회(){
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("age").lt(16).gt(12))
                .withSourceFilter(new FetchSourceFilterBuilder().withIncludes("movieName", "age").build())
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        search.get().forEach(a-> System.out.println(a));
        List<Movie> collect = search.get().map(a -> a.getContent()).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void 레스트템플로_operatorminimum_조회(){
        //operator는 문장 검색할때 and연산
        //minimum_should_match는 or연산 일 때 몇개 이상 맞는 경우 데이터를 조회 한다.
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("movieName", "햇반을 구하기").operator(Operator.AND))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        search.get().forEach(a-> System.out.println(a));
        List<Movie> collect = search.get().map(a -> a.getContent()).collect(Collectors.toList());
        System.out.println(collect);

        NativeSearchQuery build2 = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("movieName", "햇반을 구하기").operator(Operator.OR))//기본이 or조건
                .build();

        SearchHits<Movie> search2 = elasticsearchRestTemplate.search(build2, Movie.class);
        search2.get().forEach(a-> System.out.println(a));
        List<Movie> collect2 = search2.get().map(a -> a.getContent()).collect(Collectors.toList());
        System.out.println(collect2);

        NativeSearchQuery build1 = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("movieName", "햇반을 구하기").minimumShouldMatch("2"))
                .build();

        SearchHits<Movie> search1 = elasticsearchRestTemplate.search(build1, Movie.class);
        search1.get().forEach(a-> System.out.println(a));
        List<Movie> collect1 = search1.get().map(a -> a.getContent()).collect(Collectors.toList());
        System.out.println(collect1);

        NativeSearchQuery build3 = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("movieName", "햇반을 구하기").minimumShouldMatch("1"))
                .build();

        SearchHits<Movie> search3 = elasticsearchRestTemplate.search(build3, Movie.class);
        search3.get().forEach(a-> System.out.println(a));
        List<Movie> collect3 = search3.get().map(a -> a.getContent()).collect(Collectors.toList());
        System.out.println(collect3);
    }

    @Test
    public void 레스트템플_match_all(){
        //match_all은 인덱스 안의 모든 doc을 조회
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_match_query(){
        //match는 텍스트, 숫자, 날짜 등이 포함된 문장을 행태소 분석으로 텀으로 분리한 후 질의를 수행
        //주로 검색어를 분석해야 할 경우 사용한다.
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("movieName", "삼진그룹 타짜"))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_multi_match(){
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("인생", "movieName", "movieeEnName"))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_term(){
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("actors.name", "신민아"))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_bool(){
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("genre", Genre.COMEDY))
                        .filter(QueryBuilders.rangeQuery("age").gt(10).lt(17)))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_query_string(){
        NativeSearchQuery movieEnName = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("(The AND Mask)").defaultField("movieEnName"))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(movieEnName, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_prefix(){
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.prefixQuery("movieEnName", "mask"))
                //.withQuery(QueryBuilders.prefixQuery("movieEnName", "The"))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(build, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_exit(){
        new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.existsQuery("movieEnName"))
                .build();
    }

    @Test
    public void 레스트템플_nested(){
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("actors.name", "고아성"))
                .withQuery(QueryBuilders.termQuery("actors.gender", Gender.WOMAN))
                .build();

        SearchHits<Movie> search = elasticsearchRestTemplate.search(nativeSearchQuery, Movie.class);
        System.out.println(search);
        search.get().forEach(a-> System.out.println(a.getContent()));

        System.out.println("=============================================");

        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.nestedQuery("scores", QueryBuilders.termQuery("scores.rater", "REPORTER"), ScoreMode.None))
                .withQuery(
                        QueryBuilders.nestedQuery("scores", QueryBuilders.termQuery("scores.point", 7.49), ScoreMode.None))
                .build();

        SearchHits<Movie> search1 = elasticsearchRestTemplate.search(build, Movie.class);
        System.out.println(search1);
        search1.get().forEach(a-> System.out.println(a.getContent()));
    }

    @Test
    public void 레스트템플_multi_index(){
    }

}
