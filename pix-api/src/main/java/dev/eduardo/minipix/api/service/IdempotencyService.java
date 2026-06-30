package dev.eduardo.minipix.api.service;

import dev.eduardo.minipix.api.exception.TransactionNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    @Value("${pix.idempotency.ttl-hours}")
    private long ttlHours;

    public void checkAndStore(String idempotencyKey) {
        var stored = redisTemplate.opsForValue()
                .setIfAbsent(idempotencyKey, "processed", Duration.ofHours(ttlHours));

        if (Boolean.FALSE.equals(stored)) {
            throw new TransactionNotAllowedException("Duplicate transaction — idempotency key already used");
        }
    }
}
