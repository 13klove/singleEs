package com.es.sample.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.es.sample.demo", excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX, pattern = { ".*EsRepository" })})
public class JpaConfig {
}
