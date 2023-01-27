package com.throttling.limiters.configurations;


import com.google.gson.Gson;
import com.throttling.limiters.entities.LimitersConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@RefreshScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
@EnableScheduling
@Slf4j
@Getter
public class ThrottlingConfigurationManager {


    LimitersConfiguration limiters;
    ArrayList<RateLimiterConfig> rateLimiters;

    public ThrottlingConfigurationManager(@Value("${limiters}") String limiters) {
        this.limiters = new Gson().fromJson(limiters, LimitersConfiguration.class);
        this.rateLimiters = new ArrayList<>();
        initRateLimiters();

//        this.rateLimiters.add(new DBThrottlingConfig(this.limiters.getLimiter("DB")));
//        this.rateLimiters.add(new FileThrottlingConfig(this.limiters.getLimiter("FILE")));
    }

    private void initRateLimiters() {
        this.limiters.getLimiters().forEach((limiter, config) -> this.rateLimiters.add(new ThrottlingConfig(limiter, config)));
    }


}
