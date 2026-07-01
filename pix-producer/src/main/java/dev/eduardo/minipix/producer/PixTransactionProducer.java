package dev.eduardo.minipix.producer;

import dev.eduardo.minipix.PixTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
@Slf4j
public class PixTransactionProducer {
    private final KafkaTemplate<String, PixTransaction> kafkaTemplate;

    @Value("${pix.kafka.topic.transactions}")
    private String topic;

    public CompletableFuture<Void> send(PixTransaction transaction, long startTimeNanos) {
        return kafkaTemplate.send(topic, transaction.getEndToEndId(), transaction)
                .thenAccept(result -> log.info(
                        "Published transaction {} to partition {} at offset {} — elapsed={}ms",
                        transaction.getEndToEndId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        (System.nanoTime() - startTimeNanos) / 1_000_000
                )).exceptionally(ex -> {
                    log.error("Failed to publish transaction {}", transaction.getEndToEndId(), ex);
                    throw new RuntimeException(ex);
                });
    }
}
