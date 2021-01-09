package com.es.sample.demo.score.model.es;

import com.skel.pro.common.score.status.Rater;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "movie")
public class Score {

    private Rater rater;

    private Double point;

}
