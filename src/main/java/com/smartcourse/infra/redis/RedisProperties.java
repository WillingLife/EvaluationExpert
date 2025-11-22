package com.smartcourse.infra.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.redis")
public class RedisProperties {

    /**
     * Redis host 地址，允许直接填写 redis:// 或 rediss:// 前缀。
     */
    private String host = "localhost";

    /**
     * Redis 端口；当 host 带有 redis:// 前缀时忽略。
     */
    private int port = 6379;

    /**
     * Redis 认证密码，可为空。
     */
    private String password;

    /**
     * Redis 数据库索引。
     */
    private int database = 0;

    /**
     * 题目查询缓存相关配置。
     */
    private QuestionQueryCache questionQuery = new QuestionQueryCache();

    @Data
    public static class QuestionQueryCache {
        /**
         * 缓存 key 前缀。
         */
        private String keyPrefix = "query-question-";

        /**
         * 缓存失效时间（分钟）。
         */
        private long ttlMinutes = 5;
    }
}
