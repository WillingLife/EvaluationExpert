package com.smartcourse.infra.redis;

import com.smartcourse.infra.redis.dto.ExamSessionDTO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * 使用 Redis 缓存题目 ES 查询结果的简易工具。
 */
@Component
@RequiredArgsConstructor
public class ExamSessionRedisRepository {

    private static final String DEFAULT_KEY_PREFIX = "exam-session-";

    private final RedissonClient redissonClient;
    private final RedisProperties redisProperties;


    /**
     * 将题目 ES 查询结果列表写入指定 key。
     *
     * @param key    Redis 键
     * @param values 待缓存的数据
     */
    public void save(String key, ExamSessionDTO values) {
        RBucket<ExamSessionDTO> bucket = redissonClient.getBucket(withPrefix(key));
       if(values==null){
           bucket.delete();
           return;
       }
        long ttlMinutes = redisProperties.getQuestionQuery().getTtlMinutes();
        if (ttlMinutes > 0) {
            bucket.set(values, Duration.ofMinutes(ttlMinutes));
        } else {
            bucket.set(values);
        }
    }

    /**
     * 根据 key 读取缓存，若不存在则返回初始对象
     *
     * @param key Redis 键
     * @return 查询结果列表或空列表
     */
    public ExamSessionDTO get(String key) {
        Assert.hasText(key, "Redis key must not be blank");
        RBucket<ExamSessionDTO> bucket = redissonClient.getBucket(withPrefix(key));
        ExamSessionDTO data = bucket.get();
        return data != null ? data : new ExamSessionDTO();
    }

    private String withPrefix(String key) {
        String prefix = redisProperties.getQuestionQuery().getKeyPrefix();
        if (!StringUtils.hasText(prefix)) {
            prefix = DEFAULT_KEY_PREFIX;
        }
        return prefix + key;
    }
}
