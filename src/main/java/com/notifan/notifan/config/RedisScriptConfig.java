package com.notifan.notifan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Loads the sliding-window rate limiter's Lua script once at startup, rather than sending the
 * script text on every call.
 */
@Configuration
public class RedisScriptConfig {

    @Bean
    public RedisScript<Long> rateLimitScript() {
        return RedisScript.of(new ClassPathResource("scripts/sliding-window-rate-limiter.lua"), Long.class);
    }

}
