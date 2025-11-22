package com.smartcourse.infra.redis;

import com.smartcourse.pojo.vo.question.QuestionQueryESItemVO;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * 使用 Redis 缓存题目 ES 查询结果的简易工具。
 */
@Component
public class QuestionQueryESCacheRepository {

    private static final String DEFAULT_KEY_PREFIX = "query-question-";

    private final RedissonClient redissonClient;
    private final RedisProperties redisProperties;

    public QuestionQueryESCacheRepository(RedissonClient redissonClient,
                                          RedisProperties redisProperties) {
        this.redissonClient = redissonClient;
        this.redisProperties = redisProperties;
    }

    /**
     * 将题目 ES 查询结果列表写入指定 key。
     *
     * @param key    Redis 键
     * @param values 待缓存的数据
     */
    public void save(String key, List<QuestionQueryESItemVO> values) {
        Assert.hasText(key, "Redis key must not be blank");
        RBucket<List<QuestionQueryESItemVO>> bucket = redissonClient.getBucket(withPrefix(key));
        if (CollectionUtils.isEmpty(values)) {
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
     * 根据 key 读取缓存，若不存在则返回空列表。
     *
     * @param key Redis 键
     * @return 查询结果列表或空列表
     */
    public List<QuestionQueryESItemVO> get(String key) {
        Assert.hasText(key, "Redis key must not be blank");
        RBucket<List<QuestionQueryESItemVO>> bucket = redissonClient.getBucket(withPrefix(key));
        List<QuestionQueryESItemVO> data = bucket.get();
        return data != null ? data : Collections.emptyList();
    }

    private String withPrefix(String key) {
        String prefix = redisProperties.getQuestionQuery().getKeyPrefix();
        if (!StringUtils.hasText(prefix)) {
            prefix = DEFAULT_KEY_PREFIX;
        }
        return prefix + key;
    }
}
