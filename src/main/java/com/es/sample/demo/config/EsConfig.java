package com.es.sample.demo.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.skel.pro.api", excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX, pattern = { ".*JpaRepository" })})
public class EsConfig extends AbstractElasticsearchConfiguration {

    private final static String HOST = "192.168.74.130";
    private final static String PORT = "9200";

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(HOST+":"+PORT)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

}
