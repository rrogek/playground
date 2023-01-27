package com.throttling.limiters.configurations;

import com.throttling.limiters.entities.LimitConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ThrottlingConfig implements RateLimiterConfig {

    LimitConfig limitConfig;

    String id;

    Map<String, Bucket> typesLimiters;

    Bucket rateLimiter;

    public ThrottlingConfig(String id, LimitConfig config) {
        this.id = id;
        this.limitConfig = config;
        this.typesLimiters = new ConcurrentHashMap<>();
        initLimiter();
        initOverrides();
    }

    private void initOverrides() {
        if (hasOverrides()) {
            Map<String, LimitConfig.QuotaTimeConfig> overrides = this.limitConfig.getTypes();
            for (Map.Entry<String, LimitConfig.QuotaTimeConfig> checker : overrides.entrySet()) {
                log.info("Creating new rate limiter for [{}], configurations [{}]",
                        checker.getKey(),
                        checker.getValue());

                Bandwidth limit = Bandwidth.classic(checker.getValue().getQuota(),
                        Refill.intervally(checker.getValue().getQuota(),
                                checker.getValue().getTimeIndicatorDuration()));
                LocalBucket rateLimiter = Bucket.builder()
                        .addLimit(limit)
                        .build();
                typesLimiters.put(checker.getKey(), rateLimiter);
            }
        }
    }

    private void initLimiter() {
        Bandwidth limit = Bandwidth.classic(this.limitConfig.getLimits().getQuota(),
                Refill.intervally(this.limitConfig.getLimits().getQuota(),
                        this.limitConfig.getLimits().getTimeIndicatorDuration()));
        this.rateLimiter = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public String getIdentifier() {
        return this.id;
    }

    @Override
    public boolean hasOverrides() {
        return limitConfig.getTypes().size() > 0;
    }

    @Override
    public Map<String, Bucket> getOverridesLimiters() {
        return typesLimiters;
    }

    @Override
    public Bucket getRateLimiter() {
        return this.rateLimiter;
    }

}
