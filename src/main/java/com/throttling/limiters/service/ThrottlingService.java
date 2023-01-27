package com.throttling.limiters.service;

import com.throttling.limiters.configurations.RateLimiterConfig;
import com.throttling.limiters.configurations.ThrottlingConfigurationManager;
import com.throttling.limiters.entities.Message;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@Slf4j
public class ThrottlingService {

    final ThrottlingConfigurationManager throttlingConfiguration;

    public ThrottlingService(ThrottlingConfigurationManager throttlingConfiguration) {
        this.throttlingConfiguration = throttlingConfiguration;
    }


    public boolean canConsume(Message message) {
        log.info("Validating can consume message [{}]", message);
        ArrayList<RateLimiterConfig> rateLimiters = this.throttlingConfiguration.getRateLimiters();

        for (RateLimiterConfig checker : rateLimiters) {
            log.info("searching for overrides under [{}]", checker.getIdentifier());
            if (checker.hasOverrides() && checker.getOverridesLimiters().containsKey(message.getType())) {
                log.info("Overriding default limiter quota since [{}] is in place", message.getType());
                Bucket rateLimiterType = checker.getOverridesLimiters().get(message.getType());
                if (!rateLimiterType.tryConsume(1)) {
                    log.warn("Cannot consume message ... quota was reached for [{}]", message.getType());
                    return false;
                }
                return true;
            } else {
                Bucket rateLimiter = checker.getRateLimiter();
                if (!rateLimiter.tryConsume(1)) {
                    log.warn("Cannot consume message ... quota was reached was [{}]!", checker.getIdentifier());
                    return false;
                }
            }
        }
        return true;
    }


//    TODO - being used only for testing purposes...
    @Scheduled(fixedDelay = 5000)
    public void scheduleFixedDelayTask() {
        Message message = Message.builder()
                .content("bla bla" + UUID.randomUUID())
                .customerId("Tenant_UUID.randomUUID()")
                .type("Connectivity")
                .id(UUID.randomUUID().toString())
                .build();

        Message message2 = Message.builder()
                .content("bla bla" + UUID.randomUUID())
                .customerId("Tenant_UUID.randomUUID()")
                .type("System")
                .id(UUID.randomUUID().toString())
                .build();

        canConsume(message);
        canConsume(message2);
    }

}
