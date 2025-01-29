package com.tenpo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.io.Serializable;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Serializable> reactiveRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Serializable> valueSerializer = new Jackson2JsonRedisSerializer<>(Serializable.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Serializable> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, Serializable> context = builder
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(redisConnectionFactory, context);
    }
}