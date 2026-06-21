package com.rahulsmgv.parkinglot.service.impl;

import com.rahulsmgv.parkinglot.service.CacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Stub for cache initialization. Actual cache loading is handled by startup configuration.
     */
    @Override
    public void initializeCache() {
        // Cache preloading is handled elsewhere during startup.
    }

    /**
     * Read a numeric cache counter from Redis.
     */
    @Override
    public Long getCounter(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? 0L : Long.parseLong(value.toString());
    }

    /**
     * Increment a numeric Redis counter using Redis atomic increment semantics.
     */
    @Override
    public void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    /**
     * Decrement a numeric Redis counter using Redis atomic decrement semantics.
     */
    @Override
    public void decrement(String key) {
        redisTemplate.opsForValue().decrement(key);
    }
}