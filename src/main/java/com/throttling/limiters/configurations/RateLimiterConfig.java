package com.throttling.limiters.configurations;

import io.github.bucket4j.Bucket;

import java.util.Map;

public interface RateLimiterConfig {

    boolean hasOverrides();

    String getIdentifier();

    Bucket getRateLimiter();

    Map<String, Bucket> getOverridesLimiters();

}
