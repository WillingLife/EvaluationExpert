package com.smartcourse.infra.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 创建全局可复用的 {@link RedissonClient}，便于在任意位置注入使用。
 * 如果未配置 Redis 属性则使用默认值，保证应用可直接启动。
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfig {

    private final RedisProperties redisProperties;

    public RedissonConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setAddress(buildAddress(redisProperties.getHost(), redisProperties.getPort()))
                .setDatabase(redisProperties.getDatabase());

        if (StringUtils.hasText(redisProperties.getPassword())) {
            singleServerConfig.setPassword(redisProperties.getPassword());
        }

        return Redisson.create(config);
    }

    private String buildAddress(String host, int port) {
        if (StringUtils.hasText(host) && (host.startsWith("redis://") || host.startsWith("rediss://"))) {
            return host;
        }
        return String.format("redis://%s:%d", host, port);
    }
}
