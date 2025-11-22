package com.smartcourse.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "custom.ai.deepseek")
public class DeepSeekProperties {
    private String baseUrl;
    private String apiKey;
    private String model;
}
