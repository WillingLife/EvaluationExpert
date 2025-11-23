package com.smartcourse.infra.ai;

import com.smartcourse.properties.DeepSeekProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DeepSeekProperties.class)
@RequiredArgsConstructor
public class AiModelConfig {
    private final DeepSeekProperties deepSeekProperties;

    @Bean
    public OpenAiApi deepseekAiApi() {
        return OpenAiApi.builder()
                .baseUrl(deepSeekProperties.getBaseUrl())
                .apiKey(deepSeekProperties.getApiKey())
                .build();
    }

    @Bean("deepseekChatModel")
    public OpenAiChatModel deepseekChatModel(OpenAiApi deepseekAiApi) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(deepSeekProperties.getModel())
                .temperature(0.8)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(deepseekAiApi)
                .defaultOptions(options)
                .build();
    }
}
