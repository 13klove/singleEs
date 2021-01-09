package com.es.sample.demo.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.skel.pro.common"})
@EnableJpaRepositories(basePackages = "com.skel.pro.api", excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX, pattern = { ".*EsRepository" })})
public class JpaConfig {
}
