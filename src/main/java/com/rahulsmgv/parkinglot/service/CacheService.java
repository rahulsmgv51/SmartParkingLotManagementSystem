package com.rahulsmgv.parkinglot.service;

public interface CacheService {

    void initializeCache();

    Long getCounter(String key);

    void increment(String key);

    void decrement(String key);
}