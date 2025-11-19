package com.smartcourse.infra.http.dify;

import com.smartcourse.infra.http.dify.annotations.GradeShortQuestionClient;
import com.smartcourse.properties.DifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
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
}
