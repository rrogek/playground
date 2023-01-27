package com.throttling.limiters.entities;

import lombok.Getter;

import java.util.Map;

public class LimitersConfiguration {

    @Getter
    Map<String, LimitConfig> limiters;



    public LimitConfig getLimiter(String identifier) {
        return limiters.get(identifier);
    }
}
