package com.smartcourse.infra.http.dify;

import com.smartcourse.infra.http.dify.annotations.*;
import com.smartcourse.properties.DifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(DifyProperties.class)
@RequiredArgsConstructor
public class DifyClientConfiguration {
    private final DifyProperties difyProperties;

    @Bean
    @GradeShortQuestionClient
    public DifyClient gradeShortQuestionClient(){
        RestClient restClient = RestClient.builder()
                .baseUrl(difyProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + difyProperties.getGradeShortQuestionKey())
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(DifyClient.class);
    }

    @Bean
    @PolishAssignmentClient
    public DifyClient polishAssignmentClient(){
        RestClient restClient = RestClient.builder()
                .baseUrl(difyProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + difyProperties.getPolishAssignmentKey())
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(DifyClient.class);
    }

    @Bean
    @MappingKnowledgeClient
    public DifyClient mappingKnowledge(){
        RestClient restClient = RestClient.builder()
                .baseUrl(difyProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + difyProperties.getMappingKnowledgeKey())
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(DifyClient.class);
    }

    @Bean
    @ExamGenerateQueryClient
    public DifyClient examGenerateQueryClient(){
        WebClient webClient = WebClient.builder()
                .baseUrl(difyProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " +difyProperties.getExamGenerateQueryKey())
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
        return factory.createClient(DifyClient.class);
    }

    @Bean
    @GradeAssignmentClient
    public DifyClient gradeAssignmentClient(){
        RestClient restClient = RestClient.builder()
                .baseUrl(difyProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + difyProperties.getGradeAssignmentKey())
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(DifyClient.class);
    }
}
