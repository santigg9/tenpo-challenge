package com.tenpo.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.time.Duration;

@Service
public class RedisService {

    private final ReactiveRedisTemplate<String, Serializable> redisTemplate;
    private static final Duration CACHE_DURATION = Duration.ofMinutes(30); // Configure as needed


    public RedisService(ReactiveRedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> setKey(String key, Serializable value) {
        return redisTemplate.opsForValue()
                .set(key, value, CACHE_DURATION);
    }

    public Mono<Serializable> getKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }


}
