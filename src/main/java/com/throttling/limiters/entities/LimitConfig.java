package com.throttling.limiters.entities;

import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.util.Map;

@Getter
@ToString
public class LimitConfig {

    QuotaTimeConfig limits;

    Map<String, QuotaTimeConfig> types;

    public QuotaTimeConfig getType(String identifier) {
        return this.types.get(identifier);
    }

    @Getter
    @ToString
    public class Type {
        QuotaTimeConfig limits;

    }

    @Getter
    @ToString
    public class QuotaTimeConfig {
        String timeIndicator;
        int quota;


        public Duration getTimeIndicatorDuration() {
            switch (timeIndicator) {
                case "HOURS":
                    return Duration.ofHours(1);
                case "MINUTES":
                    return Duration.ofMinutes(1);
                case "DAYS":
                    return Duration.ofDays(1);
                default:
                    return Duration.ofSeconds(1);
            }
        }


    }

}
