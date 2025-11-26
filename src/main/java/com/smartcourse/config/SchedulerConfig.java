package com.smartcourse.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "dev.scheduling.enable", havingValue = "true", matchIfMissing = true)
public class SchedulerConfig {
}
